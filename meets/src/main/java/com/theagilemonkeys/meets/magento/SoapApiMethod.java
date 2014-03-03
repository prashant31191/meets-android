package com.theagilemonkeys.meets.magento;

import android.util.Log;

import com.theagilemonkeys.meets.ApiMethod;
import com.theagilemonkeys.meets.utils.soap.SoapParser;

import org.jdeferred.Deferred;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public class SoapApiMethod<RESULT> extends ApiMethod<RESULT> {
    private static final String HTTPS_PREFIX = "https://";
    private static String apiSessionId;
    private static final Object sessionIdLock = new Object();

    public static final String API_SESSION_EXPIRED_FAULTCODE = "5";

    //These properties need to be configured on app init
    public static String baseUrl;
    public static String soapApiUser;
    public static String soapApiPass;
    public static String soapNamespace;

    private SoapSerializationEnvelope soapEnvelope;
    private HttpTransportSE httpTransport;

    public SoapApiMethod(Class magentoModelClass) {
        super(magentoModelClass);
    }
    @Override
    public Deferred run(Map<String, Object> params, List<String> urlExtraSegments) {
        httpTransport = new HttpTransportSE(baseUrl);
        soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = false;
        soapEnvelope.xsd = SoapSerializationEnvelope.XSD;
        soapEnvelope.enc = SoapSerializationEnvelope.ENC;
        return super.run(params, urlExtraSegments);
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

    protected void parseResponse(Object response, RESULT model) throws Exception {
        SoapParser.parse((SoapObject) response, model);
    }

    @Override
    public RESULT loadDataFromNetwork() throws Exception {
        ensureApiLogin();

        Object res = send(getMethodName(), params);

        if ( SoapParser.isPrimitiveOrInmutable(modelClass) ){
            return (RESULT) res;
        }
        else{
            RESULT model = (RESULT) modelClass.newInstance();
            parseResponse(res, model);
            return model;
        }
    }

    private Object send(String method, Map<String, Object> params) throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(soapNamespace, method);

        if ( params == null)
            params = new HashMap<String, Object>();

        if (apiSessionIsValid())
            params.put("sessionId", getApiSessionId());

        for (Map.Entry<String,Object> entry : params.entrySet()){
            String key = entry.getKey();
            Object val = entry.getValue();
            if ( val instanceof KvmSerializable ){
                request.addProperty(buildPropertyInfo(key, (KvmSerializable) val));
            }
            else{
                request.addProperty(key, val);
            }
        }

        soapEnvelope.setOutputSoapObject(request);

        List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
        String basicAuthName = getBasicAuthName();
        String basicAuthPass = getBasicAuthPass();
        if (basicAuthName != null && basicAuthPass != null) {
            byte[] token = (basicAuthName + ":" + basicAuthPass).getBytes();
            headerList.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode(token)));
        }
        // This is due to a ksoap error. Sometimes we need to close connection and restart.
        headerList.add(new HeaderProperty("Connection", "Close"));


        httpTransport.call("", soapEnvelope, headerList);

        try {
            return soapEnvelope.getResponse();
        }
        catch (SoapFault e) {
            if (API_SESSION_EXPIRED_FAULTCODE.equals(e.faultcode)) {
                // Session expired. Set apiSessionId to null and let it reconnect on retry
                setApiSessionId(null);
                Log.d(getClass().getSimpleName(), "Soap api session expired. Trying to reconnect");
            }
            throw e;
        }
    }

    private Object send(String method, String... params) throws IOException, XmlPullParserException {
        Map<String, Object> finalParams = new HashMap<String, Object>();

        if ( params != null )
            for(int i = 0; i < params.length; i += 2)
                finalParams.put(params[i], params[i+1]);

        return send(method, finalParams);
    }

    private PropertyInfo buildPropertyInfo(String name, KvmSerializable val) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName(name);
        propertyInfo.setValue(val);
        propertyInfo.setType(val.getClass());
        return propertyInfo;
    }

    private void ensureApiLogin() throws IOException, XmlPullParserException {
        synchronized (sessionIdLock) {
            if ( getApiSessionId() == null )
                setApiSessionId((String) send("login", "username", soapApiUser, "apiKey", soapApiPass));
        }

    }

    private static void setApiSessionId(String id) {
        synchronized (sessionIdLock) {
            apiSessionId = id;
        }
    }

    private static String getApiSessionId() {
        synchronized (sessionIdLock) {
            return apiSessionId;
        }
    }


    private static boolean apiSessionIsValid(){
        synchronized (sessionIdLock) {
            return apiSessionId != null;
        }
    }
}
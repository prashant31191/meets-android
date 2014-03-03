![Meets logo](logo_android_meets.png "Meets")

# Overview
Android Meets is a native SDK designed to ease communication between mobile apps and Magento stores.
It allows you to access Magento's data as if it were local data.

You don't have to worry about learning Magento's SOAP and REST APIs or dealing with low level details.
Instead, you can only focus on building a rich native mobile shopping experience.

# Main features
- Native library that works with your project out of the box.
- Allows you to forget complexities and focus on what really matters.
- Access to Magento users, categories and products as if they were local data.
- Easily implement a fully native shopping experience.
- Continuously updated [API documentation](http://meets.io/docs) with examples.
- Direct contact with Meets programmers to resolve issues.

# Learn more
You can learn more about Meets in the official web page, <http://meets.io/>, and in the [API documentation](http://meets.io/docs)

# How to install
> **WARNING:** _Note that this is a alpha version of Meets so it's not intended to use in productions environments. We are working hard to have a stable version as soon as possible._

We are working hard to make the installation process as easy as possible. Future releases will be published in Maven central repository
so you will only have to add the Meets library dependency to your project.

Right now you have to follow this steps:

1. Download the `.jar` file from "Downloads" section.
1. Put it inside `libs/` directory of your android project.
1. Now you have to add Meets library and its dependencies in your `build.gradle` file, inside the `dependencies` section:

        dependencies {
           // ....
           // Meets library
           compile files('libs/meets<version>.jar') // Replace <version> so that it matches with the name of the downloaded file

           // Meets dependencies
           compile 'com.octo.android.robospice:robospice:1.4.9'
           compile 'com.octo.android.robospice:robospice-google-http-client:1.4.9'
           compile 'com.google.code.ksoap2-android:ksoap2-android:3.2.0'
           compile 'com.google.api-client:google-api-client-jackson2:1.17.0-rc'
           compile 'org.jdeferred:jdeferred-core:1.1.0'
           // ...
        }

1. Add the `MeetsSpiceService` service to your AndroidManifest.xml file:

        <manifest ...>
            ...
            <application ...>
                ...
                <service android:name="com.theagilemonkeys.meets.MeetsSpiceService" />
                ...
            </application>
        </manifest>

1. Finally, be sure you have `ACCESS_NETWORK_STATE` and `INTERNET` permissions in your AndroidManifest.xml file:

        <manifest ...>
            ...
            <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
            <uses-permission android:name="android.permission.INTERNET" />
        </manifest>

# Meets at Meet Magento Spain conference

![Meet Magento Spain logo](http://es.meet-magento.com/wp-content/themes/Fest/images/style1/logo.png "Meet Magento Spain")

Meets have been announced at [Meet Magento Spain](http://es.meet-magento.com/), a Magento eCommerce conference
where merchants, Magento agencies, Magento service provider and the Magento community exchange knowledge and
experiences with enthusiastic decision maker and experts according the topics Magento and eCommerce.

[Meets at Meet Magento Spain.](http://es.meet-magento.com/meets/)

# Open source projects that have help Meets to come a reality


# License


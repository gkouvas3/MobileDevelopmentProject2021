# MobileDevelopmentProject2021
A Social Media Network (SMN) Aggregator for Android.

## Getting Started

The app uses both facebook and twitter api's.

### Prerequisites
api keys from https://developers.facebook.com and https://developer.twitter.com .

User must have already installed the native facebook and instagram apps. Also, user must own a facebook page and a professional instagram account linked to that page.

### Known Bugs

This is an early alpha and has several bugs.

Login Activity
```
When opening the app, if you are previewsly logged in, you have to press the facebook button once and then log in again.
Sometimes the callback from twitter login is not successful.
```

Search Posts Activity
```
Facebook does not support any kind of public posts search. 
Also, sometimes the list of posts generated is not entirely full. 
Sometimes, when clicking to a post to open it to browser, the app crushes.
```

Create Post Activity
```
Not uploading images to twitter. Text-only tweets are currently supported.
For posting a photo to instagram, an extra step is required.
```

Create Story Activity

```
At the time, twitter does not seems to support api calls for creating moments.
```

## Built With

* Facebook Android SDK
* Facebook Share SDK
* Firebase Authentication
* Firebase Database
* Firebase Storage
* Jackson Android Networking
* Daniel DeGroff's OAuth1AuthorizationHeaderBuilder

## Author

* **Athanasios Gkouvas** - Applied Informatics Student at [University of Macedonia](https://uom.gr)



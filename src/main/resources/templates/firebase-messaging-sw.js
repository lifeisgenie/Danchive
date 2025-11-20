importScripts("https://www.gstatic.com/firebasejs/12.6.0/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/12.6.0/firebase-messaging-compat.js");

firebase.initializeApp({
    apiKey: "AIzaSyB65FzpCMwn9sTgrwTr74VTQrPzp4rBipo",
    authDomain: "danchive.firebaseapp.com",
    projectId: "danchive",
    storageBucket: "danchive.firebasestorage.app",
    messagingSenderId: "333648318334",
    appId: "1:333648318334:web:08c98e5b3c08088bfded2f",
});

const messaging = firebase.messaging();
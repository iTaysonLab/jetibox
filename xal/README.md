# XAL wrapper
> XAL means "Xbox Live Authentication Library", according to Microsoft

MS uses OAuth 2.0 specification for signing into an account. However, it relies on fetching device tokens, SHA-256 hashes and other scary (probably) stuff - so, to make things much easier I decided to "take" the official XAL library, which does all this stuff on it's own.

It also provides secure user credentials storage with WebView auth process.

## What this module does
This module is basically Java-files from the official companion application, which has been decompiled by [jadx](https://github.com/skylot/jadx), paired with native library.

## Privacy/Security measures
The native library actually collects some analytics data + the package name, so there is a risk of a ban in case MS decided to take some action (I hope no).

## Plans
In future this module _could_ be replaced with a Java/Kotlin-only MS OAuth 2.0 implementation. On the other sides, fixes and improvements in Java-side are planned.
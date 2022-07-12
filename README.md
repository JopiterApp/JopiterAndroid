# Jopiter Android

[![GitHub](https://img.shields.io/github/license/JopiterApp/JopiterAndroid)](LICENSE)

# Building APK
## Official Build

⚠️ You need the files

- jopiter-key.jks
- keystore.properties

unencripted and available at the root of the project to sign the release

The official Jopiter APP is build using

```
./gradlew bundleOfficial
```

The bundle output will be in `app/build/outputs/bundle/officialRelease`

## Unnoficial Build

An unsigned unnoficial build can be used instead of the original. Keep in mind this will not allow
you to update the app unless you sign with the same key.

```
./gradlew bundleUnofficial
```
The bundle output will be in `app/build/outputs/bundle/unofficialRelease`

# License

[![GitHub](https://img.shields.io/github/license/JopiterApp/JopiterAndroid)](LICENSE)

See full license in the [LICENSE File](LICENSE)

> The AGPL license differs from the other GNU licenses in that it was built for network software. You can distribute modified versions if you keep track of the changes and the date you made them. As per usual with GNU licenses, you must license derivatives under AGPL. It provides the same restrictions and freedoms as the GPLv3 but with an additional clause which makes it so that source code must be distributed along with web publication. Since web sites and services are never distributed in the traditional sense, the AGPL is the GPL of the web.

![img.png](docs/agpl-summary.png)

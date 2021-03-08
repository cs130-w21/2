# Repository Template

[![Build Status](https://travis-ci.org/cs130-w21/template.svg?branch=master)](https://travis-ci.org/cs130-w21/template)
[![Release](https://img.shields.io/github/v/release/cs130-w21/template?label=release)](https://github.com/cs130-w21/template/releases/latest)

## Workflows

### Creating releases

Releases are shown in the tags section of the repository. A release can be triggered by simply tagging a commit in the repository. Once the commit is tagged, the build scripts will run and after a few minutes the apk file will appear in the tag.

### Using a CI/CD pipeline

This project uses Travis CI to manage CI/CD and builds. All pushes and pull requests trigger builds, but only tagging commits on master will trigger releases. The latest releases can be found under tags.

### Building without Travis CI

The recommended way to build and run the app outside of Travis CI is to use Android Studio. With Android Studio you can simply open the `Pathways` directory as a project and click run to run the application in a debug mode on a virtual device or a normal device. 

To trigger a debug build without using Travis CI or Android Studio, run `./gradlew assembleDebug` in the Pathways directory. The APK will appear in the directory `/2/Pathways/app/build/outputs/apk/debug/`. Do not rename the APK or it will fail. 

Because the application build must be signed and keystore usernames and passwords are encrypted in Travis, only a debug build can be created without re-signing the application. A release build will require you to [sign the app](https://developer.android.com/studio/publish/app-signing). Then, similarly you can use `./gradlew assembleRelease` or follow other instructions to build in Android Studio. The cleanest way to build a release is to allow Travis CI to run the script in Github or by restarting a previous build on [travis-ci.com](https://travis-ci.com/). Signing the app without Travis will require updates to gradle files in the application so the appropriate keystore credentials can be obtained.

### Running Tests

To run tests for Pathways, execute `./gradlew test` in the `Pathways` directory.
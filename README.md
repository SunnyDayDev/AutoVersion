# AutoVersion
Automated handling of version code, name and release notes for Android.

# How to use

Add to your build.gradle:

```
{

    apply plugin: 'me.sunnydaydev.autoversion'
    
    ...
    
    autoVersion {
        
        prepareVersionOnTasks "assembleRelease", "assembleBetaRelease" // Any tasks which need to prepeare version
        
    }

    android {

        ...

        defaultConfig {

            versionCode autoVersion.versionCode
            versionName autoVersion.versionName

            ...

        }

    }
    
}
```

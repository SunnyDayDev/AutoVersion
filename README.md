# AutoVersion
Automated handling of version code, name and release notes for Android.

# How to use

Add to your build.gradle:

```
{

    apply plugin: 'me.sunnydaydev.autoversion'
    
    ...
    
    autoVersoin {
        
        autoVersionForTasks "assembleRelease", "assembleBetaRelease" // Any tasks which need to prepeare version
        
    }
    
}
```
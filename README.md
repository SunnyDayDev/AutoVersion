# AutoVersion
Automated handling of version code, name and release notes for Android.

Version name scheme:
x.x.x (for example 0.0.1)

Custom version names will be added at next releases.

# How to use
Add to your build.gradle:
```
{

    apply plugin: 'me.sunnydaydev.autoversion'
    
    buildscript {

        repositories {
            mavenCentral()
        }

        dependencies {
            classpath 'me.sunnydaydev:autoversion:0.0.8'
        }
    }
    
    ...
    
    autoVersion {
    
        increments {
    
            anyName {
            
                priority 1
                
                // Choose where you want to autoincrement
                onVariants "flavTwoDebug"
                onTasks "assembleFlavOneReleaseProduct"
                onBuildTypes "releaseProduct"
                onFlavors "flavOne"
                
                versionNameIncrement "0.0.1"
                versionCodeIncrement 1
                
                // Without it it will be automatically incremented in background
                // else will shown confirm dialog
                confirmByDialog true
                updateReleaseNotes true
    
            }
    
        }
    
    }

    android {

        ...

        defaultConfig {

            versionCode autoVersion.versionCode
            versionName autoVersion.versionName

            ...

            // If you use Crashlytics or something else
            ext.betaDistributionReleaseNotesFilePath = autoVersion.releaseNotesFilePath
            
        }

    }
    
}
```
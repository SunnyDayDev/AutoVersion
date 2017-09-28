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
            classpath 'me.sunnydaydev:autoversion:0.0.3'
        }
    }
    
    ...
    
    autoVersion {
        
        prepareVersionOnTasks "assembleDebug", "assembleRelease"// Any tasks which need to prepeare version
        
    }

    android {

        ...

        defaultConfig {

            versionCode autoVersion.versionCode
            versionName autoVersion.versionName

            ...

            // If you use Crashlytics or something else
            ext.betaDistributionReleaseNotesFilePath = autoVersion.releaseNoteFilePath
            
        }

    }
    
}
```

After it Autoversion will show 'Prepare version name' dialog on each started task.

Also you can set default increment value:
```
autoVersion {

    defaultIncrement {

        // global, major, minor
        increments 0, 0, 2
        buildIncrement 1

    }

}
```

If you want to increment version name/code automatically (without dialog 
) you can use autoIncrements.
```
autoVersion {

    autoIncrements {

        anyName {
        
            //On these tasks version will be automatically incremented
            useOnTasks "assembleDebug"
            
            // global, major, minor
            increments 1, 0, 0
            
            buildIncrement 2

        }

    }

}
```
# TBS

### 使用方法:

To get a Git project into your build:

**Step 1. Add the JitPack repository to your build file**
```
	allprojects {
		repositories {
			
			maven { url 'https://jitpack.io' }
		}
	}
```
**Step 2. Add the dependency**

```
	dependencies {
	        implementation 'com.github.DREARMING:TBS:1.0.0'
	}
```

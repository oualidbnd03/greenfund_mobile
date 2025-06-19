# Building APK with GitHub Actions

This project uses GitHub Actions to automatically build Android APKs without requiring Android Studio or local setup.

## Available Workflows

### 1. Build Android APK (`build-apk.yml`)
- **Triggers**: Push to main/master/develop branches, Pull Requests, Manual dispatch
- **Outputs**: Debug and Release APKs
- **Artifacts**: Available for 30 days

### 2. Build Signed Release APK (`build-signed-apk.yml`)
- **Triggers**: Manual dispatch only
- **Outputs**: Signed Release APK (ready for Play Store)
- **Artifacts**: Available for 90 days

## How to Build APK

### Method 1: Automatic Build (Recommended)
1. **Push your code** to the `main`, `master`, or `develop` branch
2. **Go to Actions tab** in your GitHub repository
3. **Wait for the workflow to complete** (usually 5-10 minutes)
4. **Download the APK** from the workflow artifacts

### Method 2: Manual Build
1. **Go to Actions tab** in your GitHub repository
2. **Click on "Build Android APK"** workflow
3. **Click "Run workflow"**
4. **Choose build type** (debug or release)
5. **Click "Run workflow"**
6. **Wait for completion** and download the APK

### Method 3: Build Signed APK (For Production)
1. **Set up repository secrets** (see below)
2. **Go to Actions tab**
3. **Click on "Build Signed Release APK"** workflow
4. **Click "Run workflow"**
5. **Enter version details** and run

## Setting Up Repository Secrets (For Signed APK)

To build signed APKs, you need to add these secrets to your repository:

1. **Go to your repository Settings**
2. **Click on "Secrets and variables" → "Actions"**
3. **Add these repository secrets**:
   - `KEYSTORE_PASSWORD`: Your keystore password
   - `KEY_PASSWORD`: Your key password

### Example Secret Values:
```
KEYSTORE_PASSWORD: mySecurePassword123
KEY_PASSWORD: myKeyPassword456
```

## Finding Your Built APK

### In GitHub Actions:
1. **Go to Actions tab**
2. **Click on the completed workflow run**
3. **Scroll down to "Artifacts"**
4. **Click on the APK artifact to download**

### APK Locations:
- **Debug APK**: `app-debug.apk` (for testing)
- **Release APK**: `app-release.apk` (for distribution)
- **Signed APK**: `app-release-signed.apk` (for Play Store)

## Installing the APK

### On Android Device:
1. **Enable "Install from Unknown Sources"** in Settings
2. **Download the APK** from GitHub Actions artifacts
3. **Open the APK file** on your device
4. **Follow the installation prompts**

### Using ADB (for developers):
```bash
adb install app-debug.apk
```

## Troubleshooting

### Common Issues:

1. **Build fails with "SDK not found"**:
   - The workflow automatically sets up the Android SDK
   - No action needed from you

2. **Build fails with signing errors**:
   - Make sure you've set up the repository secrets
   - Use the debug build for testing

3. **APK not appearing in artifacts**:
   - Check the workflow logs for errors
   - Ensure the build completed successfully

4. **Large APK size**:
   - Debug APKs are larger than release APKs
   - Release APKs are optimized and smaller

## Workflow Features

### Automatic Features:
- ✅ **JDK 17 Setup**: Automatically configured
- ✅ **Gradle Caching**: Faster subsequent builds
- ✅ **Android SDK**: Automatically installed
- ✅ **Dependency Management**: Handled by Gradle
- ✅ **Artifact Storage**: APKs stored for easy download

### Build Types:
- **Debug**: Larger size, includes debugging info
- **Release**: Optimized, smaller size, ready for distribution
- **Signed Release**: Ready for Google Play Store

## Next Steps

1. **Push your code** to trigger the first build
2. **Download and test** the debug APK
3. **Set up secrets** for signed builds (optional)
4. **Use signed APK** for production distribution

## Support

If you encounter issues:
1. Check the workflow logs in the Actions tab
2. Ensure your code compiles without errors
3. Verify all dependencies are properly configured in `build.gradle`

---

**Note**: This workflow builds your APK in a clean Ubuntu environment, ensuring consistent builds across different machines. 
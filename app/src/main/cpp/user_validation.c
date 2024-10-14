#include <jni.h>
#include <string.h>
#include <regex.h>
#include <android/log.h>

#define LOG_TAG "UserValidation"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

JNIEXPORT jstring JNICALL
Java_com_example_recipeofthedishes_MainActivity_validateUser(JNIEnv *env, jobject obj, jstring userName, jstring userEmail, jstring userPassword) {
    const char *cUserName = (*env)->GetStringUTFChars(env, userName, 0);
    const char *cUserEmail = (*env)->GetStringUTFChars(env, userEmail, 0);
    const char *cUserPassword = (*env)->GetStringUTFChars(env, userPassword, 0);

    if (strlen(cUserName) == 0 || strlen(cUserEmail) == 0 || strlen(cUserPassword) == 0) {
        return (*env)->NewStringUTF(env, "Not all fields are filled in");
    }

    regex_t regex;
    int reti = regcomp(&regex, "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", REG_EXTENDED);
    if (reti) {
        LOGE("Could not compile regex");
        return (*env)->NewStringUTF(env, "Invalid email");
    }

    reti = regexec(&regex, cUserEmail, 0, NULL, 0);
    regfree(&regex);
    if (reti) {
        return (*env)->NewStringUTF(env, "Invalid email");
    }

    if (strlen(cUserPassword) <= 8 || !strpbrk(cUserPassword, "!@#$%^&*/")) {
        return (*env)->NewStringUTF(env, "Password must be at least 8 characters long and contain special characters");
    }


    (*env)->ReleaseStringUTFChars(env, userName, cUserName);
    (*env)->ReleaseStringUTFChars(env, userEmail, cUserEmail);
    (*env)->ReleaseStringUTFChars(env, userPassword, cUserPassword);

    return (*env)->NewStringUTF(env, "User added and signed in");
}

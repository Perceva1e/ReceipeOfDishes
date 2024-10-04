#ifndef USER_VALIDATION_H
#define USER_VALIDATION_H

#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_recipeofthedishes_MainActivity_validateUser(JNIEnv *env, jobject obj, jstring userName, jstring userEmail, jstring userPassword);

#endif // USER_VALIDATION_H
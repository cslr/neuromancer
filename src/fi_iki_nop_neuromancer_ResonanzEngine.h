/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class fi_iki_nop_neuromancer_ResonanzEngine */

#ifndef _Included_fi_iki_nop_neuromancer_ResonanzEngine
#define _Included_fi_iki_nop_neuromancer_ResonanzEngine
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     fi_iki_nop_neuromancer_ResonanzEngine
 * Method:    startRandomStimulation
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_fi_iki_nop_neuromancer_ResonanzEngine_startRandomStimulation
  (JNIEnv *, jobject, jstring, jstring);

/*
 * Class:     fi_iki_nop_neuromancer_ResonanzEngine
 * Method:    startMeasureStimulation
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_fi_iki_nop_neuromancer_ResonanzEngine_startMeasureStimulation
  (JNIEnv *, jobject, jstring, jstring, jstring);

/*
 * Class:     fi_iki_nop_neuromancer_ResonanzEngine
 * Method:    startOptimizeModel
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_fi_iki_nop_neuromancer_ResonanzEngine_startOptimizeModel
  (JNIEnv *, jobject, jstring);

/*
 * Class:     fi_iki_nop_neuromancer_ResonanzEngine
 * Method:    getOptimizeModelStatus
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_fi_iki_nop_neuromancer_ResonanzEngine_getOptimizeModelStatus
  (JNIEnv *, jobject);

/*
 * Class:     fi_iki_nop_neuromancer_ResonanzEngine
 * Method:    stopOptimizeModel
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_fi_iki_nop_neuromancer_ResonanzEngine_stopOptimizeModel
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif

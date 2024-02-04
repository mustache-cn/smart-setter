package cn.com.mustache.plugins.smart.setter.util

import com.intellij.psi.PsiClass

/**
 * Checks whether the given PsiClass has the Lombok `@Builder` annotation.
 *
 * @return `true` if the PsiClass has the `@Builder` annotation, `false` otherwise.
 */
fun PsiClass?.hasBuilderAnnotation(): Boolean {
    this ?: return false
    return this.annotations.any { it.hasQualifiedName("lombok.Builder") }
}
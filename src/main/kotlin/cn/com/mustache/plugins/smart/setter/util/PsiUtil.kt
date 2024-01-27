package cn.com.mustache.plugins.smart.setter.util

import com.intellij.psi.PsiClass

fun PsiClass?.hasBuilderAnnotation(): Boolean {
    this ?: return false
    return this.annotations.any { it.hasQualifiedName("lombok.Builder") }
}
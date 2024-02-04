package cn.com.mustache.plugins.smart.setter.entity

import com.intellij.psi.PsiClass

/**
 * Restful interface structured data.
 *
 * @param psiClass The PsiClass corresponding to the setter.
 * @param className The name of the class.
 * @param leadingSpaces The number of leading spaces.
 * @param hasBuilder Indicates whether the class has a builder.
 */
data class SetterInfo(
    val psiClass: PsiClass?,
    val className: String,
    val leadingSpaces: Int,
    val hasBuilder: Boolean
)

package com.pattexpattex.dikko.api.annotations

import net.dv8tion.jda.api.Permission

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class RequireUserPermissions(vararg val permissions: Permission)

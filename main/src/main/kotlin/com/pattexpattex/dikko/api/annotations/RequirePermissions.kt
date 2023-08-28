package com.pattexpattex.dikko.api.annotations

import net.dv8tion.jda.api.Permission

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class RequirePermissions(vararg val permissions: Permission)

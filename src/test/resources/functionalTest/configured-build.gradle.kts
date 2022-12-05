plugins {
    id("pt.com.hugo-dias.git-versioner")
}

versioner {
    startFrom {
        major.set(1)
        minor.set(1)
        patch.set(1)
    }
    match {
        major.set("trex")
        minor.set("stego")
        patch.set("compy")
    }
    tag {
        prefix.set("x")
    }
    pattern {
        pattern.set("%M.%m.%p(-%c)")
    }
    git {
        authentication {
            https {
                username.set("user")
                password.set("password")
            }
        }
    }
}

tasks.create("printVersionEarly") {
    versioner.apply()
    val version = project.version
    doLast {
        println(version)
    }
}

version = "0.5.0"

dependencies {
    modApi("${project.api}")

    modApi(include("${project.autoConfig}"))

    modApi(include("${project.clothConfig}"))

    modApi(include("${project.commonFormatting}"))

    modApi("${project.fabricASM}")

    modApi(include("${project.GFH}"))

    bloated("${project.modMenu}")

    modApi(include("${project.phormat}"))

//    modApi "${project.REI}"

//    implementation include("${project.shortcode}") {
//        transitive false
//    }

    modApi("${project.smartEntrypoints}")

    implementation("${project.toml4j}")

    implementation(include("${project.unsafe}"))
}

def modInclude(final String dependency) {
    dependencies {
        modApi(include(dependency))
    }
}

def bloated(final String dependency) {
    dependencies {
        modApi include(dependency) {
            transitive false
        }
    }
}

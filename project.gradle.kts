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

fun modInclude(dependency: String) {
    dependencies {
        modApi(include(dependency))
    }
}

fun bloated(dependency: String) {
    dependencies {
        modApi include(dependency) {
            transitive false
        }
    }
}

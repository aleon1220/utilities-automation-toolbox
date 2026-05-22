tasks.register<Zip>("packagePowershellScripts") {
    group = "build"
    description = "Zips all PowerShell scripts into a distributable archive"
    
    from(".")
    include("**/*.ps1", "**/*.psm1")
    archiveFileName.set("powershell-utilities.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
}

// Make the default 'build' task depend on your custom Zip task
tasks.register("build") {
    dependsOn("packagePowershellScripts")
}
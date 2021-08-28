
Build Release App: `Netbean > File > (right click) build.xml > Run target > Other targets > build-release`

Release App in folder `release/`

The release option is custom

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project name="build-ReleaseApp" default="all" basedir="..">
    <target name="build-release" depends="jar">
        <!-- Change the value of this property to be the name of your JAR,
             minus the .jar extension. It should not have spaces.
             <property name="release.jar.name" value="MyJarName"/>
        -->
        <property name="release.jar.name" value="MPlayer" />
        <!-- don’t edit below this line -->
        <property name="release.dir" value="release" />
        <property name="release.jar" value="${release.dir}/${release.jar.name}.jar" />
        <echo message="Packaging ${application.title} into a single JAR at ${release.jar}" />
        <delete dir="${release.dir}" />
        <mkdir dir="${release.dir}" />
        <jar destfile="${release.dir}/temp_final.jar" filesetmanifest="skip">
            <zipgroupfileset dir="dist" includes="*.jar" />
            <zipgroupfileset dir="dist/lib" includes="*.jar" />
            <manifest>
                <attribute name="Main-Class" value="${main.class}" />
            </manifest>
        </jar>
        <zip destfile="${release.jar}">
            <zipfileset src="${release.dir}/temp_final.jar" excludes="META-INF/*.SF, META-INF/*.DSA, META-INF/*.RSA" />
        </zip>
        <delete file="${release.dir}/temp_final.jar" />
    </target>
</project>
```

Save this file to folder `nbproject\` with name `build-release.xml`

Edit file `build.xml`. Add line `<import file="nbproject/build-release.xml"/>` to this file

If error when run app release, create new project and copy `src/` to new project and import all lib needed

Open `nbproject/project.properties`, find `javafx.main.class` and edit to: `javafx.main.class=Laucher.MPlayer`

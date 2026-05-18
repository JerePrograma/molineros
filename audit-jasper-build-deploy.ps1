$ErrorActionPreference = "Continue"

$repo   = "C:\devmolineros\ext"
$tomcat = "C:\apache-tomcat-8.5.23"
$out    = Join-Path $repo "AUDIT_JASPER_BUILD_DEPLOY.txt"

Remove-Item $out -Force -ErrorAction SilentlyContinue

function Add-Section {
    param([string]$Title)

    "`r`n============================================================" | Add-Content $out
    $Title | Add-Content $out
    "============================================================" | Add-Content $out
}

function Add-Cmd {
    param(
        [string]$Title,
        [scriptblock]$Block
    )

    Add-Section $Title

    try {
        & $Block | Out-String -Width 500 | Add-Content $out
    }
    catch {
        "ERROR: $($_.Exception.Message)" | Add-Content $out
    }
}

Add-Section "AUDITORÍA JASPER / BUILD / DEPLOY"
"Repo: $repo" | Add-Content $out
"Tomcat: $tomcat" | Add-Content $out
"Fecha: $(Get-Date)" | Add-Content $out

Add-Cmd "1) ARCHIVOS BUILD / PROPERTIES EN REPO" {
    Get-ChildItem $repo -Recurse -File |
        Where-Object {
            $_.Name -match "build.*\.xml|.*\.properties|ivy.*\.xml|pom\.xml"
        } |
        Select-Object FullName, Length, LastWriteTime |
        Sort-Object FullName
}

Add-Cmd "2) REFERENCIAS A DEPLOY / LIBS / JASPER EN CONFIGS" {
    Get-ChildItem $repo -Recurse -File |
        Where-Object {
            $_.Extension -in ".xml",".properties",".bat",".cmd",".txt"
        } |
        Select-String `
            -Pattern "lib/ext|WEB-INF/lib|ext-lib|jasperreports|commons-collections|commons-digester|commons-beanutils|commons-logging|iText|copy|todir|deploy|app.server.lib.global.dir|app.server.portal.dir|app.server.dir|app.server.parent.dir" `
            -Context 2,2 |
        ForEach-Object {
            "FILE: $($_.Path)"
            "LINE: $($_.LineNumber)"
            $_.Context.PreContext
            ">> $($_.Line)"
            $_.Context.PostContext
            ""
        }
}

Add-Cmd "3) JARS JASPER / COMMONS DENTRO DEL REPO" {
    Get-ChildItem $repo -Recurse -File -Filter "*.jar" |
        Where-Object {
            $_.Name -match "jasperreports|commons-collections|commons-digester|commons-beanutils|commons-logging|iText|itext|xalan|xerces|xml-apis"
        } |
        Select-Object Name, FullName, Length, LastWriteTime |
        Sort-Object Name, FullName
}

Add-Cmd "4) JARS JASPER / COMMONS EN TOMCAT lib" {
    Get-ChildItem "$tomcat\lib" -File -ErrorAction SilentlyContinue |
        Where-Object {
            $_.Name -match "jasperreports|commons-collections|commons-digester|commons-beanutils|commons-logging|iText|itext|xalan|xerces|xml-apis"
        } |
        Select-Object Name, FullName, Length, LastWriteTime |
        Sort-Object Name
}

Add-Cmd "5) JARS JASPER / COMMONS EN TOMCAT lib/ext" {
    Get-ChildItem "$tomcat\lib\ext" -File -ErrorAction SilentlyContinue |
        Where-Object {
            $_.Name -match "jasperreports|commons-collections|commons-digester|commons-beanutils|commons-logging|iText|itext|xalan|xerces|xml-apis"
        } |
        Select-Object Name, FullName, Length, LastWriteTime |
        Sort-Object Name
}

Add-Cmd "6) JARS JASPER / COMMONS EN ROOT WEB-INF/lib" {
    Get-ChildItem "$tomcat\webapps\ROOT\WEB-INF\lib" -File -ErrorAction SilentlyContinue |
        Where-Object {
            $_.Name -match "jasperreports|commons-collections|commons-digester|commons-beanutils|commons-logging|iText|itext|xalan|xerces|xml-apis"
        } |
        Select-Object Name, FullName, Length, LastWriteTime |
        Sort-Object Name
}

Add-Cmd "7) CATALINA common.loader / shared.loader" {
    Select-String `
        -Path "$tomcat\conf\catalina.properties" `
        -Pattern "common.loader|shared.loader|lib/ext" `
        -Context 3,3
}

Add-Cmd "8) SETENV TOMCAT" {
    if (Test-Path "$tomcat\bin\setenv.bat") {
        Get-Content "$tomcat\bin\setenv.bat"
    }
    else {
        "NO EXISTE setenv.bat"
    }
}

Add-Cmd "9) TARGETS ANT POSIBLES" {
    Get-ChildItem $repo -Recurse -File -Filter "build*.xml" |
        ForEach-Object {
            "==== $($_.FullName) ===="
            Select-String -Path $_.FullName -Pattern '<target name='
            ""
        }
}

Add-Cmd "10) TEST ReferenceMap EN lib + lib/ext" {
    $work = "$env:TEMP\jr_classpath_test"
    New-Item -ItemType Directory -Path $work -Force | Out-Null

    $javaFile = Join-Path $work "TestReferenceMap.java"

@"
public class TestReferenceMap {
    public static void main(String[] args) throws Exception {
        Class c = Class.forName("org.apache.commons.collections.ReferenceMap");
        System.out.println("OK " + c.getName() + " desde " + c.getProtectionDomain().getCodeSource().getLocation());
    }
}
"@ | Set-Content $javaFile -Encoding ASCII

    $javac = "C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe"
    $java  = "C:\Program Files\Java\jdk1.8.0_251\bin\java.exe"

    $cp = "$tomcat\lib\*;$tomcat\lib\ext\*"

    & $javac -cp $cp $javaFile
    & $java -cp "$work;$cp" TestReferenceMap
}

Add-Cmd "11) TEST ReferenceMap + JasperFillManager MISMO CLASSPATH" {
    $work = "$env:TEMP\jr_classpath_test"
    New-Item -ItemType Directory -Path $work -Force | Out-Null

    $javaFile = Join-Path $work "TestJasperClasspath.java"

@"
public class TestJasperClasspath {
    public static void main(String[] args) throws Exception {
        Class cc = Class.forName("org.apache.commons.collections.ReferenceMap");
        Class jf = Class.forName("net.sf.jasperreports.engine.JasperFillManager");
        Class st = Class.forName("net.sf.jasperreports.engine.util.JRStyledTextParser");

        System.out.println("ReferenceMap desde " + cc.getProtectionDomain().getCodeSource().getLocation());
        System.out.println("JasperFillManager desde " + jf.getProtectionDomain().getCodeSource().getLocation());
        System.out.println("JRStyledTextParser desde " + st.getProtectionDomain().getCodeSource().getLocation());
    }
}
"@ | Set-Content $javaFile -Encoding ASCII

    $javac = "C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe"
    $java  = "C:\Program Files\Java\jdk1.8.0_251\bin\java.exe"

    $cp = "$tomcat\lib\*;$tomcat\lib\ext\*;$tomcat\webapps\ROOT\WEB-INF\lib\*"

    & $javac -cp $cp $javaFile
    & $java -cp "$work;$cp" TestJasperClasspath
}

Write-Host "Auditoría generada en:" -ForegroundColor Green
Write-Host $out -ForegroundColor Green

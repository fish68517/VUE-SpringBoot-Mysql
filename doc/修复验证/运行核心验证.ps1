param([string]$CacheRoot = $env:GRADLE_USER_HOME)
$ErrorActionPreference = 'Stop'
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
if (-not $CacheRoot) { $CacheRoot = Join-Path $env:USERPROFILE '.gradle' }
$cache = Join-Path $CacheRoot 'caches\modules-2\files-2.1'
$groups = @(
    'org.jetbrains.kotlin\kotlin-compiler-embeddable\2.0.21',
    'org.jetbrains.kotlin\kotlin-stdlib\2.0.21',
    'org.jetbrains.kotlin\kotlin-script-runtime\2.0.21',
    'org.jetbrains.kotlin\kotlin-reflect\2.0.21',
    'org.jetbrains.kotlinx\kotlinx-coroutines-core-jvm\1.6.4',
    'org.jetbrains.intellij.deps\trove4j\1.0.20200330',
    'org.jetbrains\annotations\13.0'
)
$jars = @(foreach ($group in $groups) {
    $path = Join-Path $cache $group
    if (-not (Test-Path -LiteralPath $path)) { throw "缺少本地验证依赖：$path" }
    $matches = @(Get-ChildItem -LiteralPath $path -Recurse -File -Filter '*.jar' |
        Where-Object { $_.Name -notmatch '-(sources|javadoc)\.jar$' })
    if ($matches.Count -ne 1) { throw "验证依赖不唯一或缺失：$path" }
    $matches[0].FullName
})
$cp = $jars -join ';'
$root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$project = Join-Path $root '来电标记采集助手_Kotlin_Android14-16_完整项目\CallMarkCollector'
$src = Join-Path $project 'app\src\main\java\com\example\callmarkcollector'
$tests = Join-Path $project 'app\src\test\java\com\example\callmarkcollector\parser'
$jar = Join-Path $PSScriptRoot 'core-regression.jar'
# Only pure Kotlin logic. No Gradle, Android application compilation, resource build or APK.
$compilerArgs = @('-Dfile.encoding=UTF-8', '-cp', $cp, 'org.jetbrains.kotlin.cli.jvm.K2JVMCompiler',
    '-no-stdlib', '-no-reflect', '-jvm-target', '17', '-classpath', $cp, '-d', $jar,
    "$src\parser\CallScreenParser.kt", "$src\model\ParsedCallInfo.kt",
    "$src\service\RingGate.kt", "$src\service\CaptureCandidate.kt", "$tests\CaptureRegressionCases.kt")
& java @compilerArgs
if ($LASTEXITCODE -ne 0) { throw '纯 Kotlin 回归验证编译失败' }
$result = & java '-Dfile.encoding=UTF-8' -cp "$jar;$cp" com.example.callmarkcollector.parser.CaptureRegressionCasesKt
if ($LASTEXITCODE -ne 0) { throw '纯 Kotlin 回归验证失败' }
$result | Set-Content -LiteralPath "$PSScriptRoot\核心逻辑验证结果.txt" -Encoding utf8
$result
$syntax = & java '-Dfile.encoding=UTF-8' -cp $cp "$PSScriptRoot\KotlinSyntaxCheck.java" "$project\app\src"
if ($LASTEXITCODE -ne 0) { throw 'Kotlin 源码语法检查失败' }
$syntax | Set-Content -LiteralPath "$PSScriptRoot\Kotlin语法检查.txt" -Encoding utf8
$syntax
$xmls = Get-ChildItem -LiteralPath "$project\app\src\main" -Recurse -File -Filter '*.xml'
foreach ($file in $xmls) { [xml](Get-Content -LiteralPath $file.FullName -Raw -Encoding utf8) | Out-Null }
("XML parsed: " + $xmls.Count) | Set-Content -LiteralPath "$PSScriptRoot\XML检查.txt" -Encoding utf8

param(
    [string]$CacheRoot = $env:GRADLE_USER_HOME
)
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
$src = Join-Path $root '来电标记采集助手_Kotlin_Android14-16_完整项目\CallMarkCollector\app\src\main\java\com\example\callmarkcollector'
$jar = Join-Path $PSScriptRoot 'probe.jar'
& java '-Dfile.encoding=UTF-8' -cp $cp org.jetbrains.kotlin.cli.jvm.K2JVMCompiler `
    -no-stdlib -no-reflect -jvm-target 17 -classpath $cp -d $jar `
    "$src\parser\CallScreenParser.kt" "$src\model\ParsedCallInfo.kt" "$PSScriptRoot\ParserProbe.kt"
if ($LASTEXITCODE -ne 0) { throw '解析器探针编译失败' }
$result = & java '-Dfile.encoding=UTF-8' -cp "$jar;$cp" ParserProbeKt
if ($LASTEXITCODE -ne 0) { throw '解析器探针执行失败' }
$result | Set-Content -LiteralPath "$PSScriptRoot\解析结果.txt" -Encoding UTF8
$result

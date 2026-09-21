param([switch]$CheckOnly)

$ErrorActionPreference = 'Stop'
$hbuilderExe = 'D:\soft\HBuilderX.5.24.2026081301\HBuilderX\HBuilderX.exe'
$previousJavaOptions = [Environment]::GetEnvironmentVariable('JAVA_TOOL_OPTIONS', 'Process')

try {
    if (-not (Test-Path -LiteralPath $hbuilderExe -PathType Leaf)) {
        throw '未找到 HBuilderX，请更新 scripts/start-hbuilder-safe.ps1 中的 hbuilderExe 路径。'
    }
    $expectedPath = (Get-Item -LiteralPath $hbuilderExe).FullName
    $env:JAVA_TOOL_OPTIONS = $null
    $running = @(Get-Process -Name HBuilderX -ErrorAction SilentlyContinue | Where-Object { -not $_.HasExited })

    if ($CheckOnly) {
        if ([Environment]::GetEnvironmentVariable('JAVA_TOOL_OPTIONS', 'Process')) {
            throw '当前启动环境仍包含 JAVA_TOOL_OPTIONS。'
        }
        Write-Host '检查通过：启动环境已临时清理，HBuilderX 路径有效。'
        foreach ($item in $running) {
            $item.Refresh()
            Write-Host ("进程 {0}，窗口句柄 {1}，路径 {2}" -f $item.Id, $item.MainWindowHandle, $item.Path)
        }
        Write-Host '检查模式未结束任何进程，也未启动 HBuilderX。'
        exit 0
    }

    # 先检查全部实例，遇到可见窗口或其他安装位置时不结束任何进程。
    foreach ($item in $running) {
        $item.Refresh()
        if ($item.HasExited) { continue }
        if ($item.Path -ne $expectedPath) {
            throw "检测到其他位置或无法确认路径的 HBuilderX（PID $($item.Id)），请先手动退出该程序。"
        }
        if ($item.MainWindowHandle -ne [IntPtr]::Zero) {
            throw "HBuilderX 仍有窗口（PID $($item.Id)），请保存文件并退出，再双击启动文件。"
        }
    }

    if ($running.Count -gt 0) {
        Start-Sleep -Milliseconds 1000
        foreach ($item in $running) {
            $current = Get-Process -Id $item.Id -ErrorAction SilentlyContinue
            if (-not $current) { continue }
            $current.Refresh()
            if ($current.HasExited) { continue }
            if ($current.StartTime -ne $item.StartTime -or $current.Path -ne $expectedPath -or
                $current.MainWindowHandle -ne [IntPtr]::Zero) {
                throw 'HBuilderX 状态已变化，已停止清理，请确认窗口关闭后重试。'
            }
            Write-Host "正在结束无窗口的 HBuilderX 后台残留（PID $($current.Id)）……"
            Stop-Process -InputObject $current
            $current.WaitForExit(10000) | Out-Null
            if (-not $current.HasExited) { throw '后台进程尚未退出，请稍后重试。' }
        }
    }

    $remaining = @(Get-Process -Name HBuilderX -ErrorAction SilentlyContinue | Where-Object { -not $_.HasExited })
    if ($remaining.Count -gt 0) {
        throw '仍存在 HBuilderX 进程，未启动新实例，请退出后重试。'
    }
    Start-Process -FilePath $expectedPath -WorkingDirectory (Split-Path -Parent $expectedPath) -WindowStyle Normal
    Write-Host '已使用临时清理后的 Java 环境启动 HBuilderX。'
    Write-Host '系统环境变量未修改；请在 HBuilderX 中自行点击打包。'
} catch {
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
} finally {
    [Environment]::SetEnvironmentVariable('JAVA_TOOL_OPTIONS', $previousJavaOptions, 'Process')
}

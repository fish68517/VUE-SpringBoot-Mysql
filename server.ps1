param([switch]$NoBrowser)

$ErrorActionPreference = "Stop"
$siteRoot = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot "dist/build/h5"))
$port = 8765
$listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, $port)

function Get-ContentType([string]$path) {
    switch ([System.IO.Path]::GetExtension($path).ToLowerInvariant()) {
        ".html" { "text/html; charset=utf-8" }
        ".css"  { "text/css; charset=utf-8" }
        ".js"   { "text/javascript; charset=utf-8" }
        ".mjs"  { "text/javascript; charset=utf-8" }
        ".json" { "application/json; charset=utf-8" }
        ".pdf"  { "application/pdf" }
        ".xlsx" { "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" }
        ".svg"  { "image/svg+xml" }
        ".png"  { "image/png" }
        ".jpg"  { "image/jpeg" }
        default  { "application/octet-stream" }
    }
}

try {
    $listener.Start()
    $url = "http://127.0.0.1:$port/"
    # Keep this launcher ASCII-only so Windows PowerShell 5.1 can parse the
    # UTF-8 file correctly even when it has no byte-order mark (BOM).
    Write-Host "Zhongshenghui ledger started: $url" -ForegroundColor Cyan
    Write-Host "Keep this window open. Press Ctrl+C to stop." -ForegroundColor DarkGray
    if (-not $NoBrowser) {
        try {
            Start-Process $url
        } catch {
            Write-Warning "The browser could not be opened automatically. Open $url manually."
        }
    }

    while ($true) {
        $client = $listener.AcceptTcpClient()
        try {
            $stream = $client.GetStream()
            $reader = New-Object System.IO.StreamReader($stream, [System.Text.Encoding]::ASCII, $false, 4096, $true)
            $requestLine = $reader.ReadLine()
            while (($line = $reader.ReadLine()) -ne $null -and $line -ne "") { }

            $requestPath = "/"
            if ($requestLine -match "^[A-Z]+\s+([^\s]+)") { $requestPath = $Matches[1].Split('?')[0] }
            $requestPath = [System.Uri]::UnescapeDataString($requestPath).TrimStart('/')
            if ([string]::IsNullOrWhiteSpace($requestPath)) { $requestPath = "index.html" }
            $filePath = [System.IO.Path]::GetFullPath((Join-Path $siteRoot $requestPath.Replace('/', [System.IO.Path]::DirectorySeparatorChar)))

            if (-not $filePath.StartsWith($siteRoot, [System.StringComparison]::OrdinalIgnoreCase) -or -not (Test-Path $filePath -PathType Leaf)) {
                $body = [System.Text.Encoding]::UTF8.GetBytes("404 - File not found")
                $header = "HTTP/1.1 404 Not Found`r`nContent-Type: text/plain; charset=utf-8`r`nContent-Length: $($body.Length)`r`nConnection: close`r`n`r`n"
            } else {
                $body = [System.IO.File]::ReadAllBytes($filePath)
                $header = "HTTP/1.1 200 OK`r`nContent-Type: $(Get-ContentType $filePath)`r`nContent-Length: $($body.Length)`r`nCache-Control: no-cache`r`nConnection: close`r`n`r`n"
            }
            $headerBytes = [System.Text.Encoding]::ASCII.GetBytes($header)
            $stream.Write($headerBytes, 0, $headerBytes.Length)
            $stream.Write($body, 0, $body.Length)
            $stream.Flush()
        } finally {
            $client.Close()
        }
    }
} finally {
    $listener.Stop()
}

# W01 接口用例自动跑批
# 用法：
#   1. 先在另一个窗口启动服务：mvn spring-boot:run
#   2. 再在本目录执行：pwsh requests/run-cases.ps1
#   3. 结果会打印成 markdown 表格，直接粘进 docs/evidence/w01.md

param(
    [string]$Base = 'http://localhost:8080'
)

$tmp = Join-Path $env:TEMP ('w01_' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force -Path $tmp | Out-Null

function New-Body([string]$name, [string]$json) {
    $p = Join-Path $tmp "$name.json"
    Set-Content -Path $p -Encoding UTF8 -NoNewline -Value $json
    return $p
}

function Invoke-Case {
    param([string]$Method, [string]$Path, [string]$BodyFile)
    $argv = @('-s', '-w', '|||%{http_code}', '-X', $Method, ($Base + $Path))
    if ($BodyFile) { $argv += @('-H', 'Content-Type: application/json', '--data-binary', "@$BodyFile") }
    $raw = ((& curl.exe @argv) | Out-String).Trim()
    $i = $raw.LastIndexOf('|||')
    if ($i -lt 0) { return [pscustomobject]@{ Code = '000'; Body = $raw } }
    return [pscustomobject]@{
        Code = $raw.Substring($i + 3).Trim()
        Body = $raw.Substring(0, $i).Trim()
    }
}

function Shorten([string]$s, [int]$n = 70) {
    $s = $s -replace '\s+', ' '
    if ($s.Length -gt $n) { return $s.Substring(0, $n) + '…' }
    return $s
}

# ---- 请求体 ----
$bOk    = New-Body 'ok'    '{"equipment_code":"EQ-A001","site_id":"SITE-A","issue_summary":"yellow lamp","priority":"NORMAL"}'
$bBlank = New-Body 'blank' '{"equipment_code":"","site_id":"SITE-A","issue_summary":"yellow lamp","priority":"NORMAL"}'
$bBadEn = New-Body 'badEn' '{"equipment_code":"EQ-A001","site_id":"SITE-A","issue_summary":"yellow lamp","priority":"URGENT"}'
$bBadJs = New-Body 'badJs' '{"equipment_code":"EQ-A001",}'
$bNoPri = New-Body 'noPri' '{"equipment_code":"EQ-A001","site_id":"SITE-A","issue_summary":"yellow lamp"}'
$bNoEq  = New-Body 'noEq'  '{"equipment_code":"EQ-XXX","site_id":"SITE-A","issue_summary":"yellow lamp","priority":"NORMAL"}'

$rows = @()

# 1
$r = Invoke-Case GET '/api/equipment/EQ-A001'
$rows += [pscustomobject]@{ Id = 1; Input = 'GET /api/equipment/EQ-A001'; Expect = '200 + 设备 JSON'; Code = $r.Code; Body = (Shorten $r.Body) }
$knownId = $null

# 2
$r = Invoke-Case GET '/api/equipment/EQ-999'
$rows += [pscustomobject]@{ Id = 2; Input = 'GET /api/equipment/EQ-999'; Expect = '404'; Code = $r.Code; Body = (Shorten $r.Body) }

# 3 合法建单（同时捕获返回的 id，给第 7 条用）
$r = Invoke-Case POST '/api/repair-orders' $bOk
$rows += [pscustomobject]@{ Id = 3; Input = 'POST 合法建单 (EQ-A001)'; Expect = '201 + Location + id'; Code = $r.Code; Body = (Shorten $r.Body) }
try { $knownId = ($r.Body | ConvertFrom-Json).id } catch { }

# 4 equipment_code 为空
$r = Invoke-Case POST '/api/repair-orders' $bBlank
$rows += [pscustomobject]@{ Id = 4; Input = 'POST equipment_code=""'; Expect = '400'; Code = $r.Code; Body = (Shorten $r.Body) }

# 5 priority 非法枚举值
$r = Invoke-Case POST '/api/repair-orders' $bBadEn
$rows += [pscustomobject]@{ Id = 5; Input = 'POST priority="URGENT"'; Expect = '400'; Code = $r.Code; Body = (Shorten $r.Body) }

# 6 JSON 语法坏
$r = Invoke-Case POST '/api/repair-orders' $bBadJs
$rows += [pscustomobject]@{ Id = 6; Input = 'POST JSON 语法坏'; Expect = '400'; Code = $r.Code; Body = (Shorten $r.Body) }

# 7 查刚建出来的工单（依赖第 3 条拿到的 id）
if ($knownId) {
    $r = Invoke-Case GET "/api/repair-orders/$knownId"
    $rows += [pscustomobject]@{ Id = 7; Input = "GET /api/repair-orders/$knownId（第3条刚建的）"; Expect = '200 + 工单 JSON'; Code = $r.Code; Body = (Shorten $r.Body) }
} else {
    $rows += [pscustomobject]@{ Id = 7; Input = 'GET /api/repair-orders/{刚建的id}'; Expect = '200'; Code = '-'; Body = '第3条没拿到 id，跳过' }
}

# 8 查不存在的工单
$r = Invoke-Case GET '/api/repair-orders/RO-9999'
$rows += [pscustomobject]@{ Id = 8; Input = 'GET /api/repair-orders/RO-9999'; Expect = '404'; Code = $r.Code; Body = (Shorten $r.Body) }

# 9 不传 priority
$r = Invoke-Case POST '/api/repair-orders' $bNoPri
$rows += [pscustomobject]@{ Id = 9; Input = 'POST 不传 priority'; Expect = '400'; Code = $r.Code; Body = (Shorten $r.Body) }

# 10 给不存在的设备建单
$r = Invoke-Case POST '/api/repair-orders' $bNoEq
$rows += [pscustomobject]@{ Id = 10; Input = 'POST equipment_code="EQ-XXX"'; Expect = '404'; Code = $r.Code; Body = (Shorten $r.Body) }

# ---- 输出 markdown ----
"| # | 输入 | 期望 | 实际状态码 | 实际响应(截断) | 结论 |"
"|---|---|---|---|---|---|"
foreach ($row in $rows) {
    $expectCode = ($row.Expect -split ' ')[0]
    if ($row.Code -eq '-') { $verdict = '⏭ 未执行' }
    elseif ($row.Code -eq $expectCode) { $verdict = '✅ 通过' }
    else { $verdict = '❌ 失败' }
    $bodyCell = $row.Body -replace '\|', '\|'
    "| $($row.Id) | $($row.Input) | $($row.Expect) | $($row.Code) | $bodyCell | $verdict |"
}

""
"（生成时间：$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')，目标：$Base）"

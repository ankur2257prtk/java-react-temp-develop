@echo off
setlocal enabledelayedexpansion

:: Set API URL
set "URL=https://adb-2216837978015015.15.azuredatabricks.net/api/2.1/jobs/runs/get"
set "AUTH_HEADER=Authorization: Bearer YOUR_ACCESS_TOKEN"

:loop
:: Call API and get response
for /f "delims=" %%A in ('powershell -Command ^
    "$response = Invoke-RestMethod -Method Get -Uri '%URL%' -Headers @{Authorization='Bearer YOUR_ACCESS_TOKEN'}; $response | ConvertTo-Json -Depth 100"') do (
    set "response=%%A"
)

:: Check if result_state exists in response
for /f "delims=" %%B in ('powershell -Command ^
    "$json = '%response%'; $obj = ConvertFrom-Json -InputObject $json; if ($obj.PSObject.Properties.name -contains 'state' -and $obj.state.PSObject.Properties.name -contains 'result_state') { $true } else { $false }"') do (
    set "has_result_state=%%B"
)

:: If result_state exists, extract and display it
if "%has_result_state%"=="True" (
    for /f "delims=" %%C in ('powershell -Command ^
        "$json = '%response%'; $obj = ConvertFrom-Json -InputObject $json; $obj.state.result_state"') do (
        set "result_state=%%C"
    )
    
    echo { "result_state": "%result_state%" }
    exit /b 0
)

:: If result_state is not found, wait and retry
timeout /t 20 /nobreak >nul
goto :loop

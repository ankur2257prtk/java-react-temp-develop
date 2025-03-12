:loop
:: Call Databricks API using PowerShell and capture the result
for /f "delims=" %%k in ('powershell -Command ^
    "$headers = @{ Authorization='Bearer %ACCESS_TOKEN%' }; ^
    try { $response = Invoke-RestMethod -Method Get -Uri '%GET_JOB_STATUS_URL%' -Headers $headers; ^
          if ($response.PSObject.Properties.Name -contains 'state' -and $response.state.PSObject.Properties.Name -contains 'result_state') { $response.state.result_state } else { 'NOT_FOUND' } } ^
    catch { 'ERROR: ' + $_.Exception.Message }"') do (
    set "result_state=%%k"
)

:: Check if result_state is found
if "%result_state%"=="NOT_FOUND" (
    echo Result state not found, checking again...
    timeout /t 20 /nobreak
    goto :loop
)

:: Check job status
if /I "%result_state%"=="SUCCESS" (
    echo Job succeeded.
    exit /b 0
) else (
    echo Job failed with status: %result_state%
    exit /b 1
)

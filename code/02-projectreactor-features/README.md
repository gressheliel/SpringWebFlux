# Configuracion de log4j
implementation 'org.fusesource.jansi:jansi:2.4.0'
implementation 'org.apache.logging.log4j:log4j-api:3.0.0-alpha1'
implementation 'org.apache.logging.log4j:log4j-core:3.0.0-alpha1'
implementation 'org.apache.logging.log4j:log4j-slf4j2-impl:3.0.0-alpha1'
// implementation 'org.apache.logging.log4j:log4j-to-slf4j:3.0.0-alpha1'


<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">


    <Properties>
        <Property name="log4j.skipJansi">false</Property>
    </Properties>

    <Appenders>
        <Console name="ConsoleAppender" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %highlight{%-5level}{FATAL=bg_red bright_white, ERROR=red, WARN=yellow, INFO=green, DEBUG=blue, TRACE=magenta} %logger{36} - %msg%n" />
        </Console>
    </Appenders>

    <Loggers>
        <Root level="debug">
            <AppenderRef ref="ConsoleAppender"/>
        </Root>
    </Loggers>
</Configuration>
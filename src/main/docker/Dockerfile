FROM registry.cn-hangzhou.aliyuncs.com/choerodon-tools/javabase:0.6.0
COPY app.jar /gateway-helper.jar
ENTRYPOINT exec java $JAVA_OPTS  -jar /gateway-helper.jar

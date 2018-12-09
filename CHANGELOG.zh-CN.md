# Changelog

这个项目的所有显著变化都将被记录在这个文件中。

## [0.12.0]

### 新增

- 添加统计api调用次数的功能

## [0.11.0] - 2018-11-13

### 新增

- 细分了userDetails获取失败的原因
- 实现了可配置的二级缓存，默认不开启
- 添加了单元测试

### 修改

- 重构了`gateway-helper`
- 缓存改为`caffeine`
- 修改了ci文件
- 升级`choerodon-starter`依赖版本为`0.7.0.RELEASE`。
- 更新了基础镜像

### 删除

- 去除了redis

## [0.10.0] - 2018-09-27

### 新增

- 添加单元测试
- 支持oracle

### 修改

- 更新license 
- 修改了ci文件
- 更新了基础镜像
- 内部接口无法调用，返403

## [0.9.0] - 2018-08-17

### 修改

- 升级`choerodon-framework-parent`依赖版本为`0.8.0.RELEASE`。
- 升级`choerodon-starter`依赖版本为`0.6.0.RELEASE`。
- 修改了请求返回的`response header`。

## [0.8.0] - 2018-07-20

### 修复

- 添加security.oauth2.client.grant-type，修复无法正常启动的问题。
- 权限校验跳过一些管理端口的请求，修复通过不了健康检查的问题。

### 优化

- 鉴权缓存时间由1min缩短为30s

## [0.7.0] - 2018-06-22

### 修改

- 升级了chart中dbtool的版本为0.5.2。
- 升级choerodon-starter依赖版本为0.5.3.RELEASE。

### 修复

- 修复了线上管理端口有时访问不通的问题。

## [0.6.0] - 2018-06-08

### 新增

- 新增Root用户跳过权限校验。

### 修改

- 修改权限校验的逻辑，抽取项目id和组织id。
- 修改`JwtAddFilter`中获取`userDetail`方式。
- 解决`/zuul`开头的`uri`无法跳过权限校验的问题。

### 删除

- 移除关于用户组相关的代码
- 移除`ZuulRoutesProperties`这个类。


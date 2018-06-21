create table fenzhan_push_stat(
`id` BIGINT (20) COMMENT '主键ID' auto_increment,
dt varchar(10)  not null COMMENT 'yyyy-MM-dd' ,
eventId varchar(10)  not null ,
duration int not null COMMENT '1,7,30天统计',
siteId int not null,
pos int not null COMMENT '位置',
pushCount int default 0 COMMENT '推送人次',
pushUserCount int default 0 COMMENT '推送用户数量',
receiveCount int default 0  COMMENT '消息点击人次',
receiveUserCount int default 0  COMMENT '消息点击用户数量',
readUserCount int default 0 COMMENT '阅读用户数量',
rechargeMoney double default 0  COMMENT'充值成功金额',
rechargeUserCount bigint default 0  COMMENT'充值成功人数',
`dbUpdateTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
PRIMARY KEY (`id`)
)
ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '消息推送统计表';


CREATE unique INDEX index_dt_duration ON fenzhan_push_stat (dt,duration,siteId,eventId,pos)
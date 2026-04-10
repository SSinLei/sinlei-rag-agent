package com.sinlei.rag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sinlei.rag.entity.ConversationHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConversationHistoryMapper extends BaseMapper<ConversationHistory> {
}

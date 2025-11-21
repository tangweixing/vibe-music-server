// 用户余额Mapper
package cn.edu.seig.vibemusic.mapper;

import cn.edu.seig.vibemusic.model.entity.UserBalance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserBalanceMapper extends BaseMapper<UserBalance> {
    // 乐观锁更新余额（防并发）
    int updateBalanceWithVersion(@Param("balance") UserBalance balance);
}
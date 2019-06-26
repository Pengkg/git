package com.pinyougou.test;

import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @BelongsProject: pinyougou_parent
 * @BelongsPackage: com.pinyougou.test
 * @Author: pky
 * @CreateTime: 2019-05-22 15:08
 * @Description: ${Description}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
public class UserMapperTest {
    @Autowired
    UserMapper userMapper;


    @Test
    public void userMapperTest() {
        List<User> list = userMapper.select(null);
        for (User user : list) {
            System.out.println(user);
        }

    }

    @Test
    public void add() {
        User user = new User();
        user.setSex("0");
        user.setUsername("志玲");
        userMapper.insert(user);
    }

    @Test
    public void add2() {
        User user = new User();
        user.setSex("1");
        user.setUsername("老王");
        userMapper.insertSelective(user);
    }


    @Test
    public void update() {
        User user = new User();
        user.setId(38);
        user.setUsername("老头");
        userMapper.updateByPrimaryKey(user);
    }

    @Test
    public void update02() {
        User user = new User();
        user.setId(37);
        user.setUsername("zl姐姐");
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Test
    public void updateByExample() {
        User user = new User();
        user.setUsername("志玲大姐姐");
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        List ids = Arrays.asList(37, 38);
        criteria.andIn("id", ids);
        userMapper.updateByExample(user, example);
    }

    /**
     * updateByExample02:忽略空值-建议使用
     */
    @Test
    public void updateByExample02() {
        User user = new User();
        user.setUsername("志玲大姐姐");
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        List ids = Arrays.asList(37, 38);
        criteria.andIn("id", ids);
        userMapper.updateByExampleSelective(user, example);
    }


    /**
     * 主键查询
     */
    @Test
    public void findById() {
        User user = userMapper.selectByPrimaryKey(10);
        System.out.println(user);
    }


    /**
     * 查询一条
     */
    @Test
    public void findByOne() {
        User user = new User();
        user.setUsername("志玲姐姐");
        User selectOne = userMapper.selectOne(user);
        System.out.println(selectOne);
    }


    /**
     * 查询多条-javabean
     */
    @Test
    public void findByList() {
        User user = new User();
        user.setUsername("志玲姐姐");
        List<User> list = userMapper.select(user);
        for (User user1 : list) {
            System.out.println(user1);
        }

    }


    /**
     * 安条件查询--Example
     */
    @Test
    public void findByExample() {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("username", "%貂%");
        List<User> list = userMapper.selectByExample(example);
        for (User user : list) {
            System.out.println(user);
        }
    }


    /**
     * 查询总记录
     */
    @Test
    public void findTotalCount() {
        int i = userMapper.selectCount(null);
        System.out.println(i);

    }


    /**
     * 主键删除
     */
    @Test
    public void delete() {
        int i = userMapper.deleteByPrimaryKey(38);
        System.out.println(i);

    }

    /**
     * 条件删除
     */
    @Test
    public void deleteByKey() {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", "志玲姐姐");
        userMapper.deleteByExample(example);

    }


    /**
     * 分页查询
     */
    @Test
    public void findByPage() {
        PageHelper.startPage(1, 5);
        List<User> list = userMapper.select(null);
        for (User user : list) {
            System.out.println(user);
        }
        PageInfo<User> pageInfo = new PageInfo<>(list);
        System.out.println(pageInfo.getTotal());


    }
}

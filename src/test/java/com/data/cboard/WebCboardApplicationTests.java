package com.data.cboard;

import com.data.cboard.Mapper.UserMapper;
//import com.data.cboard.service.UserRepository;
import com.data.cboard.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

//@SpringApplicationConfiguration(Application.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class WebCboardApplicationTests {


	/*@Autowired
	private  BlogProperties blogProperties;

	@Test
	public  void  getHello(){
		Assert.assertEquals(blogProperties.getName(),"rudy");
		Assert.assertEquals(blogProperties.getTitle(),"sirniclolas learning springboot");
	}
	@Test
	public void contextLoads() {

	}*/
	/*@Autowired
	private UserService userSerivce;

	@Before
	public void setUp() {
		// 准备，清空user表
		userSerivce.deleteAllUsers();
	}

	@Test
	public void test() throws Exception {
		// 插入5个用户
		userSerivce.create("a", 1);
		userSerivce.create("b", 2);
		userSerivce.create("c", 3);
		userSerivce.create("d", 4);
		userSerivce.create("e", 5);

		// 查数据库，应该有5个用户
		Assert.assertEquals(5, userSerivce.getAllUsers().intValue());

		// 删除两个用户
		userSerivce.deleteByName("a");
		userSerivce.deleteByName("e");

		// 查数据库，应该有3个用户
		Assert.assertEquals(3, userSerivce.getAllUsers().intValue());

	}*/
/*
	@Autowired
	private UserRepository userRepository;

	@Test
	public void test() throws Exception {

		// 创建10条记录
		userRepository.save(new User("AAA", 10));
		userRepository.save(new User("BBB", 20));
		userRepository.save(new User("CCC", 30));
		userRepository.save(new User("DDD", 40));
		userRepository.save(new User("EEE", 50));
		userRepository.save(new User("FFF", 60));
		userRepository.save(new User("GGG", 70));
		userRepository.save(new User("HHH", 80));
		userRepository.save(new User("III", 90));
		userRepository.save(new User("JJJ", 100));

		// 测试findAll, 查询所有记录
		Assert.assertEquals(10, userRepository.findAll().size());

		// 测试findByName, 查询姓名为FFF的User
		Assert.assertEquals(60, userRepository.findByName("FFF").getAge().longValue());

		// 测试findUser, 查询姓名为FFF的User
		Assert.assertEquals(60, userRepository.findUser("FFF").getAge().longValue());

		// 测试findByNameAndAge, 查询姓名为FFF并且年龄为60的User
		Assert.assertEquals("FFF", userRepository.findByNameAndAge("FFF", 60).getName());

		// 测试删除姓名为AAA的User
		userRepository.delete(userRepository.findByName("AAA"));

		// 测试findAll, 查询所有记录, 验证上面的删除是否成功
		Assert.assertEquals(9, userRepository.findAll().size());

	}*/
	/*@Autowired
	private UserMapper userMapper;

	@Test
	@Rollback
	public void findByName() throws Exception {
		userMapper.insert("AAA", 20);
		User u = userMapper.findByName("AAA");
		Assert.assertEquals(20, u.getAge().intValue());
	}*/

	@Autowired
	private UserMapper userMapper;

	@Test
	@Rollback
	public void testUserMapper() throws Exception {
		// insert一条数据，并select出来验证
		userMapper.insert("AAA", 20);
		User u = userMapper.findByName("AAA");
		Assert.assertEquals(20, u.getAge().intValue());
		// update一条数据，并select出来验证
		u.setAge(30);
		userMapper.update(u);
		u = userMapper.findByName("AAA");
		Assert.assertEquals(30, u.getAge().intValue());
		// 删除这条数据，并select验证
		userMapper.delete(u.getId());
		u = userMapper.findByName("AAA");
		Assert.assertEquals(null, u);
	}

	@Test
	@Rollback
	public void test2UserMapper() throws Exception {
		List<User> userList = userMapper.findAll();
		for(User user : userList) {
			Assert.assertEquals(null, user.getId());
			Assert.assertNotEquals(null, user.getName());
		}
	}

}

package shixi.module;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.dao.QueryResult;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.ajax.Ajax;

import shixi.bean.Student;
import shixi.bean.Subject;
import shixi.dao.StudentDao;
import shixi.service.StudentService;

@At("/student")
@IocBean
public class StudentModule {
	@Inject
	private StudentService studentServiceImpl;
	@Inject
	private StudentDao studentDao;

	@At("/")
	@Ok("jsp:Student")
	public void toStudent(HttpServletRequest request) {

	}

	@At("/add")
	public void add(@Param("..") Student stu) {
		studentServiceImpl.add(stu);
	}

	@At("/batch")
	public void batch(List<Student> list) {
		studentServiceImpl.batch(list);
	}

	@At("/delete")
	public void delete(@Param("id") int id) {
		studentServiceImpl.delete(id);
	}

	@At("/update")
	public void update(@Param("..") Student student) {
		studentServiceImpl.updateStudent(student);
	}
	/*
	 * @At("/query")
	 * 
	 * @Ok("json") public List<Student> query(@Param("stu_id")Integer
	 * stu_id, @Param("at_class")Integer at_class) { return
	 * studentServiceImpl.query(stu_id, at_class); }
	 */

	@At("/queryPage")
	@Ok("json")
	public QueryResult query(@Param("stu_id") Integer stu_id, @Param("atclass") Integer atclass,
			@Param("name") String name, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize) {
		return studentServiceImpl.query(stu_id, atclass, name, pageNumber, pageSize);
	}

	/**
	 * 学生选课
	 * 
	 * @param sid
	 * @param cid
	 * @return
	 */
	@At("/selCourse")
	@Ok("json")
	public Object selCourse(HttpSession session,  @Param("cid") Integer cid) {
		int uid = (int) session.getAttribute("uid");
		
		List<Subject> list = studentDao.findCoursesConflict(uid,cid);
		if(list != null && !list.isEmpty()){
			String msg = "";
			for(Subject item:list){
				msg += item.getName()+":"+item.getWeekday()+" "+item.getPart()+"，";
			}
			return Ajax.fail().setMsg("你要选的课程与以下课程上课时间冲突："+msg);
		}
		try{
			studentServiceImpl.selectCourse(uid, cid);
		}
		catch(Exception exception){
			return Ajax.fail().setMsg("抱歉，该课程已经选过了。");
		}
		return Ajax.ok().setMsg("选课成功，到“我的课程”查看");
	}

	/**
	 * 学生取消选课
	 * 
	 * @param sid
	 * @param cid
	 * @return
	 */
	@At("/deSelCourse")
	@Ok("json")
	public Object deSelCourse(HttpSession session, @Param("cid") Integer cid) {
		int uid = (int) session.getAttribute("uid");

		return studentServiceImpl.deSelectCourse(uid, cid);
	}

	/**
	 * @return
	 */
	@At("/listCourses")
	@Ok("jsp:listCourses")
	public void listCourses(HttpSession session, HttpServletRequest request) {

		int uid = (int) session.getAttribute("uid");

		request.setAttribute("courses", studentServiceImpl.listCourses(uid));
	}
}

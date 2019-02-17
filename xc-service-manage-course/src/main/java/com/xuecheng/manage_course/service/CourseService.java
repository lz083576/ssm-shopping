package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {


    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CoursePicRepository coursePicRepository;

    @Autowired
    CmsPageClient cmsPageClient;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    CoursePubRepository coursePubRepository;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;




    public TeachplanNode findTeachplanList(String courseId){
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        return teachplanNode;
    }

    //获取课程根节点
    public String getTeachplanRoot(String courseId){
        Optional<CourseBase> courseBase = courseBaseRepository.findById(courseId);
        if (!courseBase.isPresent()){
            return null;
        }
        CourseBase courseBase1 = courseBase.get();
        //取出课程计划根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList==null||teachplanList.size()==0){
            //新增一个根节点
            Teachplan teachplanRoot = new Teachplan();
            teachplanRoot.setCourseid(courseId);
            teachplanRoot.setPname(courseBase1.getName());
            teachplanRoot.setParentid("0");
            teachplanRoot.setGrade("1");
            teachplanRoot.setStatus("0");
            teachplanRepository.save(teachplanRoot);
            return teachplanRoot.getId();
        }
        Teachplan teachplan = teachplanList.get(0);
        return teachplan.getId();
    }
    //添加课程计划
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan){
        if(teachplan == null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程id
        String courseid = teachplan.getCourseid();
        //页面传入的parentId
        String parentid = teachplan.getParentid();
        if(StringUtils.isEmpty(parentid)){
            //取出该课程的根结点
            parentid = this.getTeachplanRoot(courseid);
        }
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        Teachplan parentNode = optional.get();
        //父结点的级别
        String grade = parentNode.getGrade();
        //新结点
        Teachplan teachplanNew = new Teachplan();
        //将页面提交的teachplan信息拷贝到teachplanNew对象中
        BeanUtils.copyProperties(teachplan,teachplanNew);
        teachplanNew.setParentid(parentid);
        teachplanNew.setCourseid(courseid);
        if(grade.equals("1")){
            teachplanNew.setGrade("2");//级别，根据父结点的级别来设置
        }else{
            teachplanNew.setGrade("3");
        }

        teachplanRepository.save(teachplanNew);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Autowired
    CourseMapper courseMapper;
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest){
        if (courseListRequest==null){
            courseListRequest = new CourseListRequest();
        }
        if (page <= 0) {
            page = 1;
        }

        if (size <= 0) {
            size = 10;
        }
        PageHelper.startPage(page,size);
        Page<CourseInfo> courseInfo =courseMapper.findAllCourseInfo();
        long total = courseInfo.getTotal();
        List<CourseInfo> result = courseInfo.getResult();

        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setTotal(total);
        queryResult.setList(result);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    //查询分类信息
    public CategoryNode selectList(){
        return categoryMapper.selectList();
    }

    public ResponseResult addCourseBase(CourseBase courseBase) {
        if(courseBase == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CourseBase save = courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //根据ID查询我的课程
    public CourseBase getCourseBaseById(String courseId){
        Optional<CourseBase> courseBase = courseBaseRepository.findById(courseId);
        if (courseBase.isPresent()){
            return courseBase.get();
        }
        return null;
    }
    //根据id修改课程
    @Transactional
    public ResponseResult updateCourseBase(String id,CourseBase courseBase) {
        //根据id查询页面信息

        CourseBase courseBaseById = this.getCourseBaseById(id);
        if (courseBaseById!=null){
            //跟新模板
            courseBaseById.setName(courseBase.getName());
            courseBaseById.setUsers(courseBase.getUsers());
            courseBaseById.setMt(courseBase.getMt());
            courseBaseById.setSt(courseBase.getSt());
            courseBaseById.setGrade(courseBase.getGrade());
            courseBaseById.setStatus(courseBase.getStatus());
            courseBaseById.setDescription(courseBase.getDescription());
            courseBaseById.setCompanyId(courseBase.getCompanyId());
            courseBaseById.setTeachmode(courseBase.getTeachmode());
            courseBaseById.setStudymodel(courseBase.getStudymodel());
            courseBaseById.setUserId(courseBase.getUserId());
            courseBaseRepository.save(courseBaseById);
            //返回成功
            return new ResponseResult(CommonCode.SUCCESS);

        }
        //修改失败
        return new ResponseResult(CommonCode.FAIL);
    }

    @Autowired
    CourseMarketRepository courseMarketRepository;
    //根据课程id查询对应的营销信息
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }
    @Transactional
    public ResponseResult updateCourseMarket(String id,CourseMarket courseMarket) {

        //获取课程id
        CourseMarket marketById = this.getCourseMarketById(id);
        if (marketById==null){
            courseMarketRepository.save(courseMarket);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        if (marketById!=null){

            marketById.setCharge(courseMarket.getCharge());
            marketById.setEndTime(courseMarket.getEndTime());
            marketById.setPrice(courseMarket.getPrice());
            marketById.setPrice_old(courseMarket.getPrice_old());
            marketById.setQq(courseMarket.getQq());
            marketById.setValid(courseMarket.getValid());
            marketById.setStartTime(courseMarket.getStartTime());
            courseMarketRepository.save(marketById);
            //修改成功
            return new ResponseResult(CommonCode.SUCCESS);
        }

        return new ResponseResult(CommonCode.FAIL);
    }


    //添加课程图片
    public ResponseResult saveCoursePic(String courseId,String picId){
        //查询课程图片
        Optional<CoursePic> optionalPic = coursePicRepository.findById(courseId);
        CoursePic coursePic=null;
        if (optionalPic.isPresent()){
            coursePic = optionalPic.get();
        }
        //没有课程图片则新建课程图片
        if (coursePic==null){
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(picId);
        //保存图片

        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //根据id查询图片下信息
    public CoursePic findCoursepic(String courseId) {
        Optional<CoursePic> optionalPic = coursePicRepository.findById(courseId);
        if (optionalPic.isPresent()){
            CoursePic coursePic = optionalPic.get();
            return coursePic;
        }
        return null;
    }

    //删除课程图片
    //删除课程图片
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
    //执行删除，返回1表示删除成功，返回0表示删除失败
        long result = coursePicRepository.deleteByCourseid(courseId);
        if(result>0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    //课程视图查询
    public CourseView getCoruseView(String id) {
        CourseView courseView = new CourseView();
        //查询课程基本信息
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if(courseMarketOptional.isPresent()){
            CourseMarket courseMarket = courseMarketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }
        //查询课程图片信息
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if(picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            courseView.setCoursePic(picOptional.get());
        }
        //查询课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }
    //页面预览
    public CoursePublishResult preview(String id) {
        CourseBase base = this.findCourseBaseById(id);
        //准备页面信息
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);//课程预览站点
        //模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(id+".html");
        //页面别名
        cmsPage.setPageAliase(base.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        //请求cms调用远程接口添加页面
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        //瓶装页面预览路径
        String url=previewUrl+pageId;
        //返回CoursePublishResult独享，包含预览路径
        return new CoursePublishResult(CommonCode.SUCCESS,url);
    }

    //根据id查询课程基本信息
    private CourseBase findCourseBaseById(String courseId){
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if(baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return null;
    }
    @Transactional
    public CoursePublishResult publish(String id) {
        //准备信息
        CourseBase base = this.findCourseBaseById(id);
        //准备页面信息
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);//课程预览站点
        //模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(id+".html");
        //页面别名
        cmsPage.setPageAliase(base.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        //diaoyongcms一键发布接口
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);

        }
        //保存课程发布状态为已发布
        CourseBase courseBase = saveCoursePubState(id);
        if (courseBase==null){
            return new CoursePublishResult(CommonCode.FAIL,null);

        }
        //保存课程索引信息
        //先创建一个coursePub对象
        CoursePub coursePub = createCoursePub(id);
        //将coursePub对象保存到数据库
        saveCoursePub(id,coursePub);

        //保存课程缓冲信息(页面发布已经返回去了)
        //页面url
        String pageUrl = cmsPostPageResult.getPageUrl();

        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

    //更新课程发布状态
    private CourseBase saveCoursePubState(String courseId){
        CourseBase courseBase = this.findCourseBaseById(courseId);
        //更新发布状态
        courseBase.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBase);
        return save;
    }

    //将coursePub对象保存到数据库
    private CoursePub saveCoursePub(String id,CoursePub coursePub){

        CoursePub coursePubNew = null;
        //根据课程id查询coursePub
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if(coursePubOptional.isPresent()){
            coursePubNew = coursePubOptional.get();
        }else{
            coursePubNew = new CoursePub();
        }

        //将coursePub对象中的信息保存到coursePubNew中
        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(id);
        //时间戳,给logstach使用
        coursePubNew.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }
    //创建coursePub对象
    private CoursePub createCoursePub(String id){
        CoursePub coursePub = new CoursePub();
        //根据课程id查询course_base
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(id);
        if(baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            //将courseBase属性拷贝到CoursePub中
            BeanUtils.copyProperties(courseBase,coursePub);
        }

        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if(picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }

        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if(marketOptional.isPresent()){
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }

        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        String jsonString = JSON.toJSONString(teachplanNode);
        //将课程计划信息json串保存到 course_pub中
        coursePub.setTeachplan(jsonString);
        return coursePub;

    }
}

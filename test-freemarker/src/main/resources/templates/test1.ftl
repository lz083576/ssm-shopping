<!DOCTYPE html>
<html>
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<br>
遍历数据模型中的list数据
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>金额</td>
        <#--<td>出生日期</td>-->

    </tr>
    <#list stus as stu>
        <tr>
            <td>${stu_index+1}</td>
            <td <#if stu.name=='小明'>style="background:red;"</#if> >${stu.name}</td>
            <td>${stu.age}</td>
            <td>${stu.money}</td>
            <#--<td>${stu.birthday}出生日期</td>-->

        </tr>

    </#list>
</table>

遍历数据模型中的stuMap  1.在中括号中取map的key  2.在map后面直接点key
<br>
姓名：${stuMap['stu1'].name}<br>
年龄：${stuMap['stu1'].age}<br>
<br>
姓名：${stuMap.stu2.name}<br>
年龄：${stuMap.stu2.age}<br>

遍历map中的key。stuMap？keys就是key列表（是一个key）
<br>
<#list stuMap?keys as k>
姓名：${stuMap[k].name}<br>
年龄：${stuMap[k].age}<br>

</#list>
</body>
</html>
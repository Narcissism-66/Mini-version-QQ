<script setup>
import {nextTick, onMounted, reactive, ref, watch} from "vue";
import {get, post} from "@/net/index.js";
import {message} from "ant-design-vue";
import {userUserStore} from "@/stores/userStore.js";
import {useWebSocketStore} from "@/stores/WebSocketStor.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {formatDateTime} from "@/stores/formatDateTime.js";
import router from "@/router/index.js";
const [messageApi, contextHolder] = message.useMessage();
const userStore=userUserStore();
const wsStore = useWebSocketStore();
const openModel = ref(false);
const privateChatContainer = ref(null) // 私聊容器引用
const groupChatContainer = ref(null) // 群聊容器引用

const showModal = async (groupId) => {
  GroupOptions.SelectGroup=groupId;
  await getAllGroupUsers();
  openModel.value = true;
};
const handleOk =async e => {
  console.log(e);
  await JoinGroup();
  openModel.value = false;
};
const options = reactive({
  users:[],
  SelectUser:0,
  message:'',
  content:[],
  avatar:''
})
const GroupOptions=reactive({
  groups:[],
  SelectGroup:0,
  content:[],
  Users:[],

})
// 私聊头像
const getAvatar=(id)=>{
  //Promise 用于处理异步操作，resolve 和 reject 是回调函数，分别用于处理成功和失败的情况。
  return new Promise((resolve, reject) => {
    get('api/user/getAvatar',{
      id:id,
    },(message,data)=>{
      resolve(data);
    })
  });
}


// 添加消息处理函数
const handleWebSocketMessage = async (data) => {
  console.log("收到WebSocket消息:", data);
  // 解析消息格式 "用户 xxx 对你说: xxx"
  const match = data.match(/用户 (\d+) 对你说: (.+)/);
  if (match) {
    const [, fromUserId, content] = match;
    if(GroupOptions.SelectGroup===0)
    {
      if(fromUserId===options.SelectUser)
      {
        post("api/chat/ReadFriendChat",{
          sender:parseInt(fromUserId),
        },(message)=>{
          console.log("读取成功！")
        })
      }
      options.content.push({
        userId: parseInt(fromUserId),
        content: content,
      });
    }
    else {
      GroupOptions.content.push({
        userId: parseInt(fromUserId),
        content: content,
        avatar: await getAvatar(fromUserId),
      });
    }
  }
}
const GetAllUsers=()=>{
  get("/api/user/AllUser",{},(message,data)=>{
    options.users=data
  })
}
const SendChatToFriend=async ()=>{
  post("api/chat/AddChat",{
    toUserId:options.SelectUser,
    content:options.message
  },(message)=>{
    messageApi.success("发送成功！")
    options.content.push({userId : userStore.user.id,content: options.message});
    wsStore.sendMsg(options.SelectUser,options.message)
    options.message='';
  })
}
const GetChatById=()=>{
  get("api/chat/GetChatById",{
    toUserId:options.SelectUser
  },(message,data)=>{
    const ChatMessage=Array.isArray(data)?data:[data];
    ChatMessage.forEach((message)=>{
      options.content.push({userId : message.sender,content: message.content});
    })
    console.log(options.content)
  })
}
const SelectUser=async (id)=>{
  GroupOptions.SelectGroup=0;
  GroupOptions.content=[];
  options.content=[];
  options.SelectUser=id;
  GetChatById();
  const index = notification.friendsChat.findIndex(item => item.senderId === id)
  if (index !== -1) {
    notification.friendsChat.splice(index, 1)
  }
  options.avatar=await getAvatar(id);
}
const SelectGroups=async (id)=>{
  options.SelectUser=0;
  options.content=[];
  GroupOptions.content=[];
  GroupOptions.SelectGroup=id;
  GetGroupMessageByGroupId();
}
const OpenBox = () => {
  ElMessageBox.prompt('请输入群名', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(({ value }) => {
    CreateGroup(value)
    ElMessage({
      type: 'success',
      message: '你的群名是: ' + value
    });
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '取消输入'
    });
  });
};
//创建群聊
const CreateGroup=(groupName)=>{
  post("api/chat/CreateGroup",{
    groupName:groupName,
    avatar:"https://www.keaitupian.cn/cjpic/frombd/1/253/1215285637/1396751085.jpg"
  },(message)=>{
    GetGroupById();
    console.log(message);
  })
}
//加入群聊
const JoinGroup=()=>{
  GroupOptions.Users.forEach((user)=>{
    if(user.IsJoin&&user.IsEdit)
    {
      post("api/chat/JoinGroup",{
        groupId:GroupOptions.SelectGroup,
        userId:user.userId
      },(message)=>{
        console.log(user.userId+"加入群聊")
        messageApi.success("邀请成功！");
      })
    }
  })
}
//需要加入群的人
const JoinPrepareUser=(id)=>{
  const index = GroupOptions.Users.findIndex(u => u.userId === id);
  if(GroupOptions.Users[index].IsEdit===true )
  {
    GroupOptions.Users[index].IsJoin = !GroupOptions.Users[index].IsJoin;  // 或其他新值
  }
  else messageApi.info("当前用户已在群中！")
}

//获取我的群
const GetGroupById=()=>{
  get("api/chat/GetGroupById",{},(message,data)=>{
    GroupOptions.groups=data;
  })
}
//发送信息
const SendMessageToGroup=async ()=>{
  await getAllGroupUsers();
  post("api/chat/SendMessageToGroup",{
    groupId:GroupOptions.SelectGroup,
    content:options.message
  },async (message)=>{

    GroupOptions.content.push({userId:userStore.user.id,content:options.message})

    console.log(GroupOptions.Users)
    GroupOptions.Users.forEach((user) => {
        console.log("当前 user.userId:", user.userId);
        if (user.userId !== userStore.user.id&&user.IsJoin) {
          wsStore.sendMsg(user.userId, options.message);
        }
    });
    messageApi.success(message);
    options.message="";
  })
}
//发信息
const AddChat=()=>{
  if(options.SelectUser===0)
  {
    SendMessageToGroup();
  }
  else SendChatToFriend();
}

//获取群内的信息
const GetGroupMessageByGroupId= ()=>{
  get("api/chat/GetGroupMessageByGroupId",{
    groupId:GroupOptions.SelectGroup,
  },async (message,data)=>{
    const groupMessage=Array.isArray(data)?data:[data];
    for (const message of groupMessage) {
      GroupOptions.content.push({userId:message.userId,content:message.content,avatar:await getAvatar(parseInt(message.userId))})
    }
  })
}
//在群里面的人
const getAllGroupUsers = () => {
  // 异步操作结束标志
  return new Promise((resolve) => {
    GroupOptions.Users = [];
    get("api/chat/GetGroupMember", {
      groupId: GroupOptions.SelectGroup,
    }, (message, data) => {
      const GroupUsers = Array.isArray(data) ? data : [data];
      const existingUserIds = new Set(GroupUsers?.map(u => u.id) || []);

      options.users.forEach((user) => {
        const isExistingUser = existingUserIds.has(user.id);
        GroupOptions.Users.push({
          userId: user.id,
          avatar: user.avatar,
          username: user.username,
          IsJoin: isExistingUser,
          IsEdit: isExistingUser !== true,
        });
      });
      console.log("获取到的群成员:", GroupOptions.Users);
      resolve();
    });
  });
};

//----------------------------通知------------------------
const notification=reactive({
  friendsChat:[],
})
const getUserById=(id)=>{
  return new Promise((resolve)=>{
    get("api/user/getUserById",{
      id:id
    },(message,data)=>{
      resolve(data);
    })
  })
}
const GetFriendChatBySenderId=async ()=>{
  get("api/chat/GetFriendChatByToUserId",{},async (message,data)=>{
    const Chats= Array.isArray(data) ? data : [data];
    for (const chat of Chats) {
      notification.friendsChat.push({
        senderId: chat.sender,
        content: chat.content,
        user:await getUserById(chat.sender),
        time:chat.time,
        IsRead: chat.IsRead
      });
    }
  })
}

//---------------------------------操作---------------------------


onMounted(()=>{
  GetAllUsers()
  GetGroupById();
  GetFriendChatBySenderId();
  wsStore.setMessageCallback(handleWebSocketMessage)
})


//----------------------滚动到最底部----------------------
const scrollContent = (container) => {
  const observer = new MutationObserver(() => {
    // 在 DOM 更新后滚动到底部
    nextTick(() => {
      if (container.value) {
        container.value.scrollTop = container.value.scrollHeight
      }
    })
  })

  // 监听聊天容器的子元素变化
  if (container.value) {
    observer.observe(container.value, {
      childList: true,    // 监听子元素增减
      subtree: true       // 监听所有后代元素
    })
  }

  // 初始滚动到底部
  nextTick(() => {
    if (container.value) {
      container.value.scrollTop = container.value.scrollHeight
    }
  })
}

// 监听聊天内容变化
watch(() => options.content, () => {
  scrollContent(privateChatContainer)
}, { deep: true })

watch(() => GroupOptions.content, () => {
  scrollContent(groupChatContainer)
}, { deep: true })

onMounted(() => {
  scrollContent(privateChatContainer)
  scrollContent(groupChatContainer)
})
</script>

<template>
  <contextHolder/>
<div class="grid grid-cols-[2fr,7fr,3fr] w-full max-h-screen gap-2 ">
<!--  导航栏-->
  <div class="max-h-screen w-full flex flex-wrap     ">
  </div>
<!--  信息-->
  <div class="max-h-screen w-full px-2 py-6" >
    <div v-if="options.SelectUser!==0||GroupOptions.SelectGroup!==0" class="shadow-xl shadow-gray-300 rounded-xl w-full min-h-full grid grid-rows-[10fr,2fr] gap-6 p-4">
<!--      对话框-->
      <div ref="privateChatContainer" v-if="options.SelectUser!==0" class="shadow-sm shadow-gray-500 scrollbar-hide overflow-y-auto rounded-xl w-full  p-4" style="max-height: 72vh ; height: 72vh">
        <div v-for="content in options.content">
          <div v-if="content.userId===userStore.user.id" class="flex flex-wrap justify-end gap-2 p-2" >
            <p class="flex my-auto border-blue-500 border-2 p-2 rounded-xl">{{content.content}}</p>
            <a-avatar  size="large" :src="userStore.user.avatar" class="select-none cursor-pointer"></a-avatar>
          </div>
          <div v-else-if="options.SelectUser===content.userId" class="flex flex-wrap justify-start gap-2 p-2">
            <a-avatar  size="large" :src="options.avatar" class="select-none cursor-pointer"></a-avatar>
            <p class="flex my-auto border-blue-500 border-2 p-2 rounded-xl">{{content.content}}</p>
          </div>
        </div>
      </div>
      <div ref="groupChatContainer" v-else class="shadow-sm shadow-gray-500 rounded-xl w-full \ scrollbar-hide overflow-y-auto p-4" style="max-height: 72vh ; height: 72vh">
        <div v-for="content in GroupOptions.content" class=" " >
          <div v-if="content.userId===userStore.user.id" class="flex flex-wrap justify-end gap-2 p-2">
            <p class="flex my-auto border-blue-500 border-2 p-2 rounded-xl">{{content.content}}</p>
            <a-avatar  size="large" :src="userStore.user.avatar" class="select-none cursor-pointer"></a-avatar>
          </div>
          <div v-else class="flex flex-wrap justify-start gap-2 p-2">
            <a-avatar  size="large" :src="content.avatar" class="select-none cursor-pointer"></a-avatar>
            <p class="flex my-auto border-blue-500 border-2 p-2 rounded-xl ">{{content.content}}</p>
          </div>
        </div>
      </div>
<!--      输入框-->
      <div  class=" rounded-xl w-full h-full" >
        <a-textarea
          @keyup.enter="AddChat()"
          v-model:value="options.message"
          placeholder="Autosize height with minimum and maximum number of lines"
          :auto-size="{ minRows: 2, maxRows: 5 }"
          size="large"
        />
      </div>
    </div>
    <div v-else class="  rounded-xl w-full h-full  gap-2 grid grid-rows-[1fr,12fr]" >
<!--      功能区-->
      <div class="shadow-sm shadow-gray-500  rounded-xl w-full h-full p-4">
        <el-button type="text" @click="OpenBox">
          <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24" class="size-6 hover:text-blue-700 hover:scale-125 active:scale-90">
            <path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M13.322 7.209c0 .749-.237 1.442-.64 2.009a3.42 3.42 0 0 1-2.796 1.45c-1.898 0-3.436-1.55-3.436-3.46S7.988 3.75 9.886 3.75a3.445 3.445 0 0 1 3.436 3.459M2.75 18.107c0-2.677 3.545-4.774 7.136-4.774c1.432 0 2.857.333 4.053.904c1.803.86 3.084 2.26 3.082 3.87v1.143a1 1 0 0 1-1 1H3.75a1 1 0 0 1-1-1zM15.172 3.75a3.445 3.445 0 0 1 3.435 3.459c0 .749-.236 1.442-.639 2.009a3.42 3.42 0 0 1-2.796 1.45m3.452 2.569c1.536.86 2.628 2.763 2.626 4.373v2.64"/>
          </svg>
        </el-button>
      </div>
<!--      通知区-->
      <div class="shadow-sm shadow-gray-500 rounded-xl w-full    p-4 space-y-2" >
        <div @click="SelectUser(notice.senderId)" v-for="notice in notification.friendsChat"  class="cursor-pointer w-full  flex flex-nowrap gap-4 border-1 border-gray-200 rounded-xl p-2 shadow-gray-500 shadow-sm" >
          <a-avatar  size="large" :src="notice.user.avatar" class="select-none cursor-pointer flex my-auto"></a-avatar>
          <div class="grid grid-rows-[1fr,1fr] ">
            <p class="flex flex-nowrap my-auto text-sm"> {{notice.user.username}}</p>
            <p class="flex flex-nowrap my-auto text-sm">{{notice.content}}</p>
          </div>
          <div class="grid grid-rows-[1fr,1fr] ml-auto my-auto">
            <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 256 256" class="ml-auto text-red-500 size-6 shake-horizontal">
              <path fill="currentColor" d="M224 71.1a8 8 0 0 1-10.78-3.42a94.13 94.13 0 0 0-33.46-36.91a8 8 0 1 1 8.54-13.54a111.46 111.46 0 0 1 39.12 43.09A8 8 0 0 1 224 71.1M35.71 72a8 8 0 0 0 7.1-4.32a94.13 94.13 0 0 1 33.46-36.91a8 8 0 1 0-8.54-13.54a111.46 111.46 0 0 0-39.12 43.09A8 8 0 0 0 35.71 72m186.1 103.94A16 16 0 0 1 208 200h-40.8a40 40 0 0 1-78.4 0H48a16 16 0 0 1-13.79-24.06C43.22 160.39 48 138.28 48 112a80 80 0 0 1 160 0c0 26.27 4.78 48.38 13.81 63.94M150.62 200h-45.24a24 24 0 0 0 45.24 0M208 184c-10.64-18.27-16-42.49-16-72a64 64 0 0 0-128 0c0 29.52-5.38 53.74-16 72Z"/>
            </svg>
            <p class="flex flex-nowrap my-auto text-sm">{{formatDateTime(notice.time)}}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
<!-- 好友-->
  <div class=" max-h-screen w-full  p-6 ">
    <div class="shadow-md shadow-gray-500 rounded-xl w-full h-full  flex flex-wrap  gap-2  p-4">
      <div v-for="user in options.users" class="p-2 gap-4 flex flex-wrap w-full  shadow-sm shadow-gray-500 rounded-xl">
        <a-avatar  size="large" :src="user.avatar"></a-avatar>
        <p class="flex flex-nowrap my-auto">{{user.username}}</p>
        <svg v-if="options.SelectUser!==user.id" @click="SelectUser(user.id)" xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24" class="size-6 flex my-auto justify-end hover:scale-110 active:scale-90 text-blue-300">
          <g fill="none"><path fill="currentColor" d="m13.087 21.388l.645.382zm.542-.916l-.646-.382zm-3.258 0l-.645.382zm.542.916l.646-.382zm-8.532-5.475l.693-.287zm5.409 3.078l-.013.75zm-2.703-.372l-.287.693zm16.532-2.706l.693.287zm-5.409 3.078l-.012-.75zm2.703-.372l.287.693zm.7-15.882l-.392.64zm1.65 1.65l.64-.391zM4.388 2.738l-.392-.64zm-1.651 1.65l-.64-.391zM9.403 19.21l.377-.649zm4.33 2.56l.541-.916l-1.29-.764l-.543.916zm-4.007-.916l.542.916l1.29-.764l-.541-.916zm2.715.152a.52.52 0 0 1-.882 0l-1.291.764c.773 1.307 2.69 1.307 3.464 0zM10.5 2.75h3v-1.5h-3zm10.75 7.75v1h1.5v-1zm-18.5 1v-1h-1.5v1zm-1.5 0c0 1.155 0 2.058.05 2.787c.05.735.153 1.347.388 1.913l1.386-.574c-.147-.352-.233-.782-.278-1.441c-.046-.666-.046-1.51-.046-2.685zm6.553 6.742c-1.256-.022-1.914-.102-2.43-.316L4.8 19.313c.805.334 1.721.408 2.977.43zM1.688 16.2A5.75 5.75 0 0 0 4.8 19.312l.574-1.386a4.25 4.25 0 0 1-2.3-2.3zm19.562-4.7c0 1.175 0 2.019-.046 2.685c-.045.659-.131 1.089-.277 1.441l1.385.574c.235-.566.338-1.178.389-1.913c.05-.729.049-1.632.049-2.787zm-5.027 8.241c1.256-.021 2.172-.095 2.977-.429l-.574-1.386c-.515.214-1.173.294-2.428.316zm4.704-4.115a4.25 4.25 0 0 1-2.3 2.3l.573 1.386a5.75 5.75 0 0 0 3.112-3.112zM13.5 2.75c1.651 0 2.837 0 3.762.089c.914.087 1.495.253 1.959.537l.783-1.279c-.739-.452-1.577-.654-2.6-.752c-1.012-.096-2.282-.095-3.904-.095zm9.25 7.75c0-1.622 0-2.891-.096-3.904c-.097-1.023-.299-1.862-.751-2.6l-1.28.783c.285.464.451 1.045.538 1.96c.088.924.089 2.11.089 3.761zm-3.53-7.124a4.25 4.25 0 0 1 1.404 1.403l1.279-.783a5.75 5.75 0 0 0-1.899-1.899zM10.5 1.25c-1.622 0-2.891 0-3.904.095c-1.023.098-1.862.3-2.6.752l.783 1.28c.464-.285 1.045-.451 1.96-.538c.924-.088 2.11-.089 3.761-.089zM2.75 10.5c0-1.651 0-2.837.089-3.762c.087-.914.253-1.495.537-1.959l-1.279-.783c-.452.738-.654 1.577-.752 2.6C1.25 7.61 1.25 8.878 1.25 10.5zm1.246-8.403a5.75 5.75 0 0 0-1.899 1.899l1.28.783a4.25 4.25 0 0 1 1.402-1.403zm7.02 17.993c-.202-.343-.38-.646-.554-.884a2.2 2.2 0 0 0-.682-.645l-.754 1.297c.047.028.112.078.224.232c.121.166.258.396.476.764zm-3.24-.349c.44.008.718.014.93.037c.198.022.275.054.32.08l.754-1.297a2.2 2.2 0 0 0-.909-.274c-.298-.033-.657-.038-1.069-.045zm6.498 1.113c.218-.367.355-.598.476-.764c.112-.154.177-.204.224-.232l-.754-1.297c-.29.17-.5.395-.682.645c-.173.238-.352.54-.555.884zm1.924-2.612c-.412.007-.771.012-1.069.045c-.311.035-.616.104-.909.274l.754 1.297c.045-.026.122-.058.32-.08c.212-.023.49-.03.93-.037z"/><path stroke="currentColor" stroke-linecap="round" stroke-width="1.5" d="M8 9h8m-8 3.5h5.5"/></g>
        </svg>
      </div>
      <div v-for="group in GroupOptions.groups" class="p-2 gap-4 flex flex-wrap w-full  shadow-sm shadow-gray-500 rounded-xl">
        <a-avatar  size="large" :src="group.avatar"></a-avatar>
        <p class="flex flex-nowrap my-auto">{{group.groupName}}</p>
        <button  @click="showModal(group.groupId)" class=" hover:text-blue-700 hover:scale-110 active:scale-90 text-blue-300"  >
          <svg class="size-6 flex my-auto" xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
            <path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19.25 1.75v6m-3-3h6m-11.814 8.814a3.907 3.907 0 1 0 0-7.814a3.907 3.907 0 0 0 0 7.814m0 0a6.686 6.686 0 0 1 6.685 6.686m-6.685-6.686A6.686 6.686 0 0 0 3.75 20.25"/>
          </svg>
        </button>
        <a-modal v-model:open="openModel" title="Basic Modal" @ok="handleOk">
          <div v-for="user in GroupOptions.Users" class="flex flex-nowrap gap-4 p-2">
            <a-avatar  size="large" :src="user.avatar"></a-avatar>
            <p class="flex my-auto text-sm ">{{user.username}}</p>
            <svg v-if="user.IsJoin===true" @click="JoinPrepareUser(user.userId)" xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24" class="size-8 text-green-600 flex flex-nowrap justify-end ml-auto ">
              <path fill="currentColor" d="m17.55 9.887l3.89-3.897q.146-.146.347-.146t.367.146t.156.357t-.156.357l-4.038 4.019q-.243.243-.566.243t-.565-.243L15.07 8.809q-.146-.145-.155-.337q-.01-.191.155-.357q.153-.146.358-.146t.35.146zM9 11.384q-1.237 0-2.119-.88T6 8.383t.881-2.118T9 5.385t2.119.88t.881 2.12t-.881 2.118T9 11.385m-7 6.192v-.608q0-.619.36-1.158q.361-.54.97-.838q1.416-.679 2.834-1.018q1.417-.34 2.836-.34t2.837.34t2.832 1.018q.61.298.97.838q.361.539.361 1.158v.608q0 .44-.299.74q-.299.298-.74.298H3.04q-.441 0-.74-.299t-.3-.739m1 .039h12v-.647q0-.332-.215-.625q-.214-.292-.593-.494q-1.234-.598-2.545-.916T9 14.616t-2.646.318t-2.546.916q-.38.202-.593.494Q3 16.637 3 16.97zm6-7.231q.825 0 1.413-.588T11 8.384t-.587-1.412T9 6.384t-1.412.588T7 8.384t.588 1.413T9 10.384m0-2"/>
            </svg>
            <svg @click="JoinPrepareUser(user.userId)" v-else xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24" class="size-8 hover:scale-110 active:scale-90 flex flex-nowrap justify-end ml-auto">
              <path fill="currentColor" d="M18.25 13.5v-3h-3v-1h3v-3h1v3h3v1h-3v3zM9 11.384q-1.237 0-2.119-.88T6 8.383t.881-2.118T9 5.385t2.119.88t.881 2.12t-.881 2.118T9 11.385m-7 7.23V16.97q0-.619.36-1.158q.361-.54.97-.838q1.416-.679 2.834-1.018q1.417-.34 2.836-.34t2.837.34t2.832 1.018q.61.298.97.838q.361.539.361 1.158v1.646zm1-1h12v-.646q0-.332-.215-.625q-.214-.292-.593-.494q-1.234-.598-2.545-.916T9 14.616t-2.646.318t-2.546.916q-.38.202-.593.494Q3 16.637 3 16.97zm6-7.23q.825 0 1.413-.588T11 8.384t-.587-1.412T9 6.384t-1.412.588T7 8.384t.588 1.413T9 10.384m0 7.232"/>
            </svg>
          </div>
        </a-modal>
        <svg v-if="GroupOptions.SelectGroup!==group.groupId" @click="SelectGroups(group.groupId)" xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24" class="size-6 flex my-auto justify-end text-blue-300 hover:scale-110 active:scale-90">
          <g fill="none"><path fill="currentColor" d="m13.087 21.388l.645.382zm.542-.916l-.646-.382zm-3.258 0l-.645.382zm.542.916l.646-.382zm-8.532-5.475l.693-.287zm5.409 3.078l-.013.75zm-2.703-.372l-.287.693zm16.532-2.706l.693.287zm-5.409 3.078l-.012-.75zm2.703-.372l.287.693zm.7-15.882l-.392.64zm1.65 1.65l.64-.391zM4.388 2.738l-.392-.64zm-1.651 1.65l-.64-.391zM9.403 19.21l.377-.649zm4.33 2.56l.541-.916l-1.29-.764l-.543.916zm-4.007-.916l.542.916l1.29-.764l-.541-.916zm2.715.152a.52.52 0 0 1-.882 0l-1.291.764c.773 1.307 2.69 1.307 3.464 0zM10.5 2.75h3v-1.5h-3zm10.75 7.75v1h1.5v-1zm-18.5 1v-1h-1.5v1zm-1.5 0c0 1.155 0 2.058.05 2.787c.05.735.153 1.347.388 1.913l1.386-.574c-.147-.352-.233-.782-.278-1.441c-.046-.666-.046-1.51-.046-2.685zm6.553 6.742c-1.256-.022-1.914-.102-2.43-.316L4.8 19.313c.805.334 1.721.408 2.977.43zM1.688 16.2A5.75 5.75 0 0 0 4.8 19.312l.574-1.386a4.25 4.25 0 0 1-2.3-2.3zm19.562-4.7c0 1.175 0 2.019-.046 2.685c-.045.659-.131 1.089-.277 1.441l1.385.574c.235-.566.338-1.178.389-1.913c.05-.729.049-1.632.049-2.787zm-5.027 8.241c1.256-.021 2.172-.095 2.977-.429l-.574-1.386c-.515.214-1.173.294-2.428.316zm4.704-4.115a4.25 4.25 0 0 1-2.3 2.3l.573 1.386a5.75 5.75 0 0 0 3.112-3.112zM13.5 2.75c1.651 0 2.837 0 3.762.089c.914.087 1.495.253 1.959.537l.783-1.279c-.739-.452-1.577-.654-2.6-.752c-1.012-.096-2.282-.095-3.904-.095zm9.25 7.75c0-1.622 0-2.891-.096-3.904c-.097-1.023-.299-1.862-.751-2.6l-1.28.783c.285.464.451 1.045.538 1.96c.088.924.089 2.11.089 3.761zm-3.53-7.124a4.25 4.25 0 0 1 1.404 1.403l1.279-.783a5.75 5.75 0 0 0-1.899-1.899zM10.5 1.25c-1.622 0-2.891 0-3.904.095c-1.023.098-1.862.3-2.6.752l.783 1.28c.464-.285 1.045-.451 1.96-.538c.924-.088 2.11-.089 3.761-.089zM2.75 10.5c0-1.651 0-2.837.089-3.762c.087-.914.253-1.495.537-1.959l-1.279-.783c-.452.738-.654 1.577-.752 2.6C1.25 7.61 1.25 8.878 1.25 10.5zm1.246-8.403a5.75 5.75 0 0 0-1.899 1.899l1.28.783a4.25 4.25 0 0 1 1.402-1.403zm7.02 17.993c-.202-.343-.38-.646-.554-.884a2.2 2.2 0 0 0-.682-.645l-.754 1.297c.047.028.112.078.224.232c.121.166.258.396.476.764zm-3.24-.349c.44.008.718.014.93.037c.198.022.275.054.32.08l.754-1.297a2.2 2.2 0 0 0-.909-.274c-.298-.033-.657-.038-1.069-.045zm6.498 1.113c.218-.367.355-.598.476-.764c.112-.154.177-.204.224-.232l-.754-1.297c-.29.17-.5.395-.682.645c-.173.238-.352.54-.555.884zm1.924-2.612c-.412.007-.771.012-1.069.045c-.311.035-.616.104-.909.274l.754 1.297c.045-.026.122-.058.32-.08c.212-.023.49-.03.93-.037z"/><path stroke="currentColor" stroke-linecap="round" stroke-width="1.5" d="M8 9h8m-8 3.5h5.5"/></g>
        </svg>
      </div>
      <p class="min-h-full"></p>
    </div>
  </div>
</div>

</template>

<style scoped>
.shake-horizontal {
  animation: shake-horizontal 0.5s infinite ease-in-out;
}

@keyframes shake-horizontal {
  0%, 100% { transform: translateX(0); }
  20% { transform: translateX(-1px); }
  40% { transform: translateX(1px); }
  60% { transform: translateX(-1px); }
  80% { transform: translateX(1px); }
}
/* 隐藏所有浏览器滚动条 */
.scrollbar-hide::-webkit-scrollbar {
  display: none; /* Chrome/Safari/Edge */
}

.scrollbar-hide {
  -ms-overflow-style: none;  /* IE/Edge */
  scrollbar-width: none;     /* Firefox */
}




</style>

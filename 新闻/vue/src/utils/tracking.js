// 生成访客ID
const getVisitorId = () => {
    let visitorId = localStorage.getItem('visitor_id')
    if (!visitorId) {
      visitorId = 'visitor_' + Date.now()
      localStorage.setItem('visitor_id', visitorId)
    }
    return visitorId
  }
  
  // 获取浏览器信息
  const getBrowserInfo = () => {
    const ua = navigator.userAgent
    return {
      userAgent: ua,
      browser: getBrowserName(ua),
      platform: navigator.platform,
      language: navigator.language,
      screenResolution: `${window.screen.width}x${window.screen.height}`,
      timezone: Intl.DateTimeFormat().resolvedOptions().timeZone
    }
  }
  
  // 获取浏览器名称的辅助函数
  const getBrowserName = (ua) => {
    if (ua.indexOf("Firefox") > -1) {
      return "Firefox"
    } else if (ua.indexOf("Opera") > -1 || ua.indexOf("OPR") > -1) {
      return "Opera"
    } else if (ua.indexOf("Edge") > -1) {
      return "Edge"
    } else if (ua.indexOf("Chrome") > -1) {
      return "Chrome"
    } else if (ua.indexOf("Safari") > -1) {
      return "Safari"
    } else if (ua.indexOf("MSIE") > -1 || ua.indexOf("Trident/") > -1) {
      return "Internet Explorer"
    }
    return "Unknown"
  }
  
  // 用户行为追踪函数
  export const trackUserBehavior = (action, data) => {
    console.log('开始追踪用户行为:', action, data)
    
    // 获取访客ID
    const visitorId = localStorage.getItem('visitor_id') || 'visitor_' + Date.now()
    localStorage.setItem('visitor_id', visitorId)
    
    // 获取浏览器信息
    const browserInfo = {
      userAgent: navigator.userAgent,
      browser: getBrowserName(navigator.userAgent),
      platform: navigator.platform,
      language: navigator.language,
      screenResolution: `${window.screen.width}x${window.screen.height}`,
      timezone: Intl.DateTimeFormat().resolvedOptions().timeZone
    }
    
    // 构造请求数据
    const requestData = {
      visitorId,
      action,
      ...data,
      ...browserInfo
    }
    
    console.log('发送的数据:', requestData)
    
    // 发送到后端API
    fetch('/api/track', {
      method: 'POST',
      body: JSON.stringify(requestData),
      headers: {
        'Content-Type': 'application/json'
      }
    }).catch(error => {
      console.error('追踪用户行为失败:', error)
    })
  }
  
  // 更新用户画像
  const updateUserProfile = (data) => {
    const profile = JSON.parse(localStorage.getItem('user_profile') || '{}')
  
    // 1. 更新类目偏好
    if (data.category) {
      profile.categories = profile.categories || {}
      profile.categories[data.category] = (profile.categories[data.category] || 0) + 1
    }
  
    // 2. 更新价格偏好
    if (data.price) {
      profile.prices = profile.prices || []
      profile.prices.push(data.price)
      // 只保留最近的10个价格记录
      if (profile.prices.length > 10) {
        profile.prices = profile.prices.slice(-10)
      }
      // 计算平均价格
      profile.avgPrice = profile.prices.reduce((a, b) => a + b, 0) / profile.prices.length
    }
  
    // 3. 更新性别偏好
    if (data.gender) { // 'male' 或 'female'
      profile.genderPreference = profile.genderPreference || { male: 0, female: 0 }
      profile.genderPreference[data.gender] += 1
    }
  
    localStorage.setItem('user_profile', JSON.stringify(profile))
  }
  
  // 获取用户画像
  export const getUserProfile = () => {
    const profile = JSON.parse(localStorage.getItem('user_profile') || '{}')
    
    // 获取最感兴趣的类目
    const topCategory = profile.categories ? 
      Object.entries(profile.categories)
        .sort((a, b) => b[1] - a[1])[0]?.[0] 
      : null
  
    // 获取价格偏好
    const pricePreference = profile.avgPrice || null
  
    // 获取性别偏好
    const genderPreference = profile.genderPreference ? 
      (profile.genderPreference.male > profile.genderPreference.female ? 'male' : 'female')
      : null
  
    return {
      topCategory,
      pricePreference,
      genderPreference
    }
  } 
// 生成随机访客ID
function generateVisitorId() {
  return 'visitor_' + Math.random().toString(36).substr(2, 9);
}

// 获取或创建访客ID
export function getVisitorId() {
  let visitorId = localStorage.getItem('visitorId');
  if (!visitorId) {
    visitorId = generateVisitorId();
    localStorage.setItem('visitorId', visitorId);
  }
  return visitorId;
} 
import { getVisitorId } from '../utils/visitorId';

export async function trackUserBehavior(behavior) {
  const visitorId = getVisitorId();
  
  try {
    await fetch('/api/user-behaviors', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userId: visitorId,
        ...behavior
      })
    });
  } catch (error) {
    console.error('Failed to track behavior:', error);
  }
}

// 使用示例
export function trackView(category) {
  trackUserBehavior({
    viewCategory: category,
    action: 'VIEW'
  });
}

export function trackClick(gender) {
  trackUserBehavior({
    clickGender: gender,
    action: 'CLICK'
  });
}

export function trackSearch(keyword) {
  trackUserBehavior({
    searchKeyword: keyword,
    action: 'SEARCH'
  });
} 
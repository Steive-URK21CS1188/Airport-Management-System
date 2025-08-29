import React from 'react'

export default function MessageBox({ type = 'info', children }) {
  return (
    <div className={`msg msg-${type}`}>
      {children}
    </div>
  )
}

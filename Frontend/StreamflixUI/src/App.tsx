import { useState, useEffect } from 'react'
import { AuthProvider, useAuth } from './contexts/AuthContext'
import { Login } from './components/Login'
import { Register } from './components/Register'
import { Dashboard } from './components/Dashboard'

function AppContent() {
  const [showLogin, setShowLogin] = useState(true)
  const { isAuthenticated } = useAuth()

  useEffect(() => {
    if (!isAuthenticated) setShowLogin(true)
  }, [isAuthenticated])

  if (isAuthenticated) {
    return <Dashboard />
  }

  return showLogin ? (
    <Login onSwitchToRegister={() => setShowLogin(false)} />
  ) : (
    <Register onSwitchToLogin={() => setShowLogin(true)} />
  )
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}

export default App

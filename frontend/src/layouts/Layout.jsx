import { Container } from 'react-bootstrap'
import NavbarComponent from '../components/NavbarComponent.jsx'
import Footer from '../components/Footer.jsx'
import { Outlet, useLocation } from 'react-router-dom'

export default function Layout(){
  const location = useLocation();
  const isLoginPageOrRegisterPage = location.pathname === '/' || location.pathname === '/register';

  return (
    <>
      {!isLoginPageOrRegisterPage && <NavbarComponent/>}
      <Container className='my-4'>
        <Outlet/>
      </Container>
      <Footer/>
    </>
  )
}

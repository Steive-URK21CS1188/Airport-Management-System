import { Routes, Route } from 'react-router-dom'
import 'bootstrap/dist/css/bootstrap.min.css'
import Layout from './layouts/Layout'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import PlaneAllocation from './modules/plane_allocation/PlaneAllocation'
import Hangar from './modules/hangar/Hangar'
import HangarStatus from './modules/hangar_allocation/HangarStatus'
import Planes from './modules/planes/Planes'
//import OwnerList from "./modules/planes/OwnerList";
import Pilots from './modules/pilots/Pilots'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="allocation" element={<PlaneAllocation />} />
        <Route path="hangar" element={<Hangar />} />
        <Route path="hangar-status" element={<HangarStatus />} />
        <Route path="planes" element={<Planes />} />
        <Route path="pilots" element={<Pilots />} />
      </Route>
    </Routes>
  )
}

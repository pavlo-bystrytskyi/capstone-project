import 'bootstrap/dist/css/bootstrap.min.css';
import ".././style/styles.css"

import {Route, Routes, useNavigate} from "react-router-dom";
import SelectType from "./App/SelectType.tsx";
import {RegistryTypeCode, registryTypes} from "../type/RegistryType.tsx";
import {useEffect, useState} from "react";
import RegistryIdData from "../type/RegistryIdData.tsx";
import ViewPublic from "./App/View/ViewPublic.tsx";
import ViewPrivate from "./App/View/ViewPrivate.tsx";
import CreateGuest from "./App/Edit/CreateGuest.tsx";
import EditGuest from "./App/Edit/EditGuest.tsx";
import Header from "./App/Header.tsx";
import CreateUser from "./App/Edit/CreateUser.tsx";
import GuestSuccess from "./App/RegistrySuccess/GuestSuccess.tsx";
import UserSuccess from "./App/RegistrySuccess/UserSuccess.tsx";
import ViewUser from "./App/View/ViewUser.tsx";
import EditUser from "./App/Edit/EditUser.tsx";
import ViewList from "./App/View/ViewList.tsx";
import axios from "axios";
import User from "../type/User.tsx";
import emptyUser from "../type/EmptyUser.tsx";
import {Container} from "react-bootstrap";
import Home from "./App/Home.tsx";

function App() {
    const navigate = useNavigate();
    const [user, setUser] = useState<User>(emptyUser);
    const [registryTypeCode, setRegistryTypeCode] = useState<RegistryTypeCode>();
    const [registryIdData, setRegistryIdData] = useState<RegistryIdData>()

    const redirectToSuccessGuest = function (data: RegistryIdData) {
        setRegistryIdData(data);
        navigate("/success-guest");
    }

    const redirectToSuccessUser = function (data: RegistryIdData) {
        setRegistryIdData(data);
        navigate("/success-user");
    }

    const loadUser = () => {
        axios.get<User>('/api/auth/me')
            .then(response => {
                setUser(response.data);
            })
            .catch(() => {
                setUser(emptyUser);
            })
    }

    useEffect(() => {
        if (registryTypeCode && registryTypeCode === RegistryTypeCode.GUEST) navigate("/new-guest");
        if (registryTypeCode && registryTypeCode === RegistryTypeCode.CUSTOMER) navigate("/new-user");
    }, [registryTypeCode]);

    useEffect(() => {
        loadUser()
    }, []);

    return (
        <>
            <Header user={user}/>
            <Container
                fluid
                className="d-flex justify-content-center mt-5"
                style={{overflowY: 'visible'}}
            >
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/select-type"
                           element={<SelectType types={registryTypes} setRegistryType={setRegistryTypeCode}/>}/>
                    <Route path="/new-guest" element={<CreateGuest onSuccess={redirectToSuccessGuest}/>}/>
                    <Route path="/new-user" element={<CreateUser onSuccess={redirectToSuccessUser}/>}/>
                    <Route path="/edit-guest/:id" element={<EditGuest onSuccess={redirectToSuccessGuest}/>}/>
                    <Route path="/edit-user/:id" element={<EditUser onSuccess={redirectToSuccessUser}/>}/>
                    <Route path="/success-guest" element={<GuestSuccess data={registryIdData}/>}/>
                    <Route path="/success-user" element={<UserSuccess data={registryIdData}/>}/>
                    <Route path="/show-public/:id" element={<ViewPublic/>}/>
                    <Route path="/show-private/:id" element={<ViewPrivate/>}/>
                    <Route path="/show-user/:id" element={<ViewUser/>}/>
                    <Route path="/show-all" element={<ViewList user={user}/>}/>
                </Routes>
            </Container>
        </>
    )
}

export default App

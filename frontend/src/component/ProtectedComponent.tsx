import React, {useEffect, useState} from 'react';
import LoginModal from "./LoginModal.tsx";
import useAuth from "../context/UserAuth.tsx";
import {useLocation, useNavigate} from "react-router-dom";

export default function ProtectedComponent(
    {
        children
    }: {
        children: React.ReactNode;
    }
) {
    const {user} = useAuth();
    const isAuthenticated = !!user?.email;
    const [showModal, setShowModal] = useState<boolean>(false);
    const navigate = useNavigate();
    const location = useLocation();
    const handleCloseModal = () => {
        setShowModal(false);
        navigate(location.state?.from || "/");
        window.location.reload();
    };
    useEffect(() => {
        if (!isAuthenticated) {
            setShowModal(true);
        }
    }, [isAuthenticated]);

    return <>
        {
            isAuthenticated
                ? children
                : <LoginModal show={showModal} onClose={handleCloseModal}/>
        }
    </>
};

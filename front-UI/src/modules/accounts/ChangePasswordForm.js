import { Button, CircularProgress, TextField } from "@mui/material";
import React, { useState } from 'react';
import { useTranslation } from "react-i18next";

import { yupResolver } from "@hookform/resolvers/yup";
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import * as Yup from "yup";
import { changeSelfAccountPassword } from "../../api/mok/accountApi";
import ConfirmationDialog from "../../components/ConfirmationDialog";
import {Pathnames} from "../../router/Pathnames";
const ChangePasswordSchema = Yup.object().shape({
    oldPassword: Yup.string()
        .min(8, 'password_length_min')
        .max(50, 'password_length_max')
        .matches(
            /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
            "password_invalid"
        )
        .required('Password is required'),
    newPassword: Yup.string()
        .min(8, 'password_length_min')
        .max(50, 'password_length_max')
        .matches(
            /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
            "password_invalid"
        )
        .required('Password is required')
        .test('is-different', 'different_password', function (value) {
            const { oldPassword } = this.parent;
            return value !== oldPassword;
        }),
    confirmPassword: Yup.string()
        .oneOf([Yup.ref('newPassword'), null], 'passwords_not_match')
        .required('confirm_password_required'),
});
function ChangePasswordForm({account, etag, hideChange}) {
    const [dialogOpen, setDialogOpen] = useState(false);
    const navigate = useNavigate();
    const {t} = useTranslation();
    const [visibleOld, setVisibleOld] = useState(false)
    const [visibleNew, setVisibleNew] = useState(false)
    const [visibleConfirm, setVisibleConfirm] = useState(false)
    const [oldPass, setOldPass] = useState("")
    const [newPass, setNewPass] = useState("")
    const [loading, setLoading] = useState(false)
    const {
        register,
        handleSubmit,
        formState: {errors},
    } = useForm({
        resolver: yupResolver(ChangePasswordSchema),
    });

    const handleReset = async () =>{
        setLoading(true)
        const tag = etag.split('"').join("")
        const body = {
            login: account.login,
            oldPassword: oldPass,
            newPassword: newPass,
            version: account.version
          };
        changeSelfAccountPassword(body, tag).then((res) =>{
            setLoading((state)=> !state)
            hideChange((state)=> !state)
            navigate(Pathnames.auth.self);
            toast.success(t("success"), {
                position: "top-center",
            })
        }).catch(error => {
            setDialogOpen((state)=> !state)
            
            
                if (error.response.status === 500) {
                    toast.error(t("server_error"), {
                        position: "top-center",
                    })
                } else   {
                    toast.error(t(error.response.data.message), {
                        position: "top-center",
                    })
                } 
            })
        
    } 
    const proceed = ({oldPassword, newPassword}) =>{
        setOldPass(oldPassword)
        setNewPass(newPassword)
        setDialogOpen(true)
       
    }





    return (
        <div style={{display: 'flex', justifyContent: 'center', alignContent: 'center', marginTop: '3rem'}}>
           
               
                   <form>
                    <TextField  sx={{mb: 4}}
                            {...register("oldPassword")}
                            type={visibleOld ? "text" : "password"}
                            variant='outlined'
                            color='secondary'
                            label={t("oldPassword")}
                            fullWidth
                            required
                            error={errors.oldPassword}
                            helperText={t(errors.oldPassword?.message)}
                            InputProps={
                                {
                                    endAdornment: <Button onClick={() => setVisibleOld(!visibleOld)}>{visibleOld ?
                                        <VisibilityOffIcon fontSize="small" sx={{color: 'black'}}/> :
                                        <VisibilityIcon fontSize="small" sx={{color: 'black'}}/>}</Button>
                                }
                            }
                        />
                          <TextField  sx={{mb: 4}}
                           {...register("newPassword")}
                           type={visibleNew ? "text" : "password"}
                            variant='outlined'
                            color='secondary'
                            label={t("newPassword")}
                            fullWidth
                            required
                            error={errors.newPassword}
                            helperText={t(errors.newPassword?.message)}
                            InputProps={
                                {
                                    endAdornment: <Button onClick={() => setVisibleNew(!visibleNew)}>{visibleNew ?
                                        <VisibilityOffIcon fontSize="small" sx={{color: 'black'}}/> :
                                        <VisibilityIcon fontSize="small" sx={{color: 'black'}}/>}</Button>
                                }
                            }
                        />
                        <TextField  sx={{mb: 4}}
                            {...register("confirmPassword")}
                            type={visibleConfirm ? "text" : "password"}
                            variant='outlined'
                            color='secondary'
                            label={t("confirm_password")}
                            fullWidth
                            required
                            error={errors.confirmPassword}
                            helperText={t(errors.confirmPassword?.message)}
                            InputProps={
                                {
                                    endAdornment: <Button onClick={() => setVisibleConfirm(!visibleConfirm)}>{visibleConfirm ?
                                        <VisibilityOffIcon fontSize="small" sx={{color: 'black'}}/> :
                                        <VisibilityIcon fontSize="small" sx={{color: 'black'}}/>}</Button>
                                }
                            }
                        />
                       {
                        loading ? <CircularProgress/> :
                            <Button fullWidth sx={{mb: 2}}
                                    onClick={handleSubmit(proceed)} type='submit'
                                    variant='contained'>{t("change_password")}</Button>
                    }
                    </form>
  
                    <ConfirmationDialog
                            open={dialogOpen}
                            title={t("confirm_change_password")}
                            actions={[
                                {label: t("proceed"), handler:handleReset, color: 'primary'},
                                {label: t("cancel"), handler: () => setDialogOpen(false), color: 'secondary'},
                            ]}
                            onClose={() => setDialogOpen(false)}
                        />
       <ToastContainer/>
        </div>
    );
}

export default ChangePasswordForm
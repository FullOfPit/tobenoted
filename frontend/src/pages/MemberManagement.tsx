import React from "react";
import {Stack} from "@mui/material";
import MemberCard from "../components/MemberCard"
import EditButton from "../components/buttons/EditButton";
import DeleteButton from "../components/buttons/DeleteButton";
import AddButton from "../components/buttons/AddButton";
import useStaffMembers from "../hooks/useStaffMembers";

export default function MemberManagement() {

    const {staff} = useStaffMembers();
    const deleteMember = staff.deleteMember;
    const addMember = staff.addMember;

    return (
        <Stack
            spacing={2}
            justifyContent={"flex-start"}
            alignItems={"center"}
            sx={{paddingTop: 20}}
        >
            {staff.staffList.map((member) =>
                <MemberCard
                    key={member.id}
                    user={member}
                    buttons={[<EditButton/>, <DeleteButton id={member.id} methodFunction={deleteMember}/>]}
                /> )
            }
            <AddButton id={"henlo"} methodFunction={addMember}/>
        </Stack>
    )
}